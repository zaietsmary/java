package ua.cinema.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.model.Actor;
import ua.cinema.repository.ActorRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActorService extends ActorRepository {

    private static final Logger logger = LoggerFactory.getLogger(ActorService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    public ActorService() {
        super();
    }

    // --- Асинхронне паралельне завантаження з JSON ---
    public CompletableFuture<Void> loadActorsParallel(String jsonFilePath) {
        logger.info("⏳ Loading actors in parallel from {}", jsonFilePath);

        return CompletableFuture.runAsync(() -> {
            try {
                List<Actor> actors = objectMapper.readValue(
                        Path.of(jsonFilePath).toFile(),
                        new TypeReference<List<Actor>>() {}
                );

                actors.parallelStream().forEach(actor -> {
                    try {
                        add(actor);
                        logger.info("Loaded actor: {} {}", actor.getFirstName(), actor.getLastName());
                    } catch (Exception e) {
                        logger.error("Failed to add actor: {}", actor, e);
                    }
                });

                logger.info(" Finished loading {} actors", actors.size());
            } catch (IOException e) {
                throw new RuntimeException("Failed to load actors JSON file: " + jsonFilePath, e);
            }
        }, executor);
    }

    // --- Паралельний пошук за ім'ям ---
    public List<Actor> findByFirstNameParallel(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) return List.of();
        String searchTerm = firstName.trim().toLowerCase();
        return getAll().parallelStream()
                .filter(a -> a.getFirstName().toLowerCase().contains(searchTerm))
                .toList();
    }

    // --- Асинхронний пошук за ім'ям ---
    public CompletableFuture<List<Actor>> findByFirstNameAsync(String firstName) {
        return CompletableFuture.supplyAsync(() -> findByFirstName(firstName), executor);
    }

    // --- Паралельне сортування ---
    public List<Actor> sortByNameParallel() {
        return getAll().parallelStream()
                .sorted(Comparator.comparing(Actor::getLastName)
                        .thenComparing(Actor::getFirstName)
                        .thenComparingInt(Actor::getBirthYear))
                .toList();
    }

    // --- Асинхронне сортування ---
    public CompletableFuture<List<Actor>> sortByNameAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<Actor> sortedList = new ArrayList<>(getAll());
            sortedList.sort(
                    Comparator.comparing(Actor::getLastName)
                            .thenComparing(Actor::getFirstName)
                            .thenComparingInt(Actor::getBirthYear)
            );
            logger.info("Async sorting of actors finished. Total actors sorted: {}", sortedList.size());
            return sortedList;
        }, executor);
    }

    // --- Закриття ExecutorService ---
    public void shutdownExecutor() {
        executor.shutdown();
        logger.info("Executor for ActorService has been shut down");
    }
}
