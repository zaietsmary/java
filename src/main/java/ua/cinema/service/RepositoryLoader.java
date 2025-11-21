package ua.cinema.service;

import ua.cinema.repository.GenericRepository;
import ua.cinema.serializer.DataSerializer;
import ua.cinema.exception.DataSerializationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RepositoryLoader {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryLoader.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    // Завантаження даних у репозиторій (як раніше)
    public <T> CompletableFuture<Void> loadAsync(
            GenericRepository<T> repository,
            DataSerializer<T> serializer,
            String filePath,
            Class<T> clazz
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Loading {} from file: {}", clazz.getSimpleName(), filePath);
                return serializer.deserialize(filePath, clazz);
            } catch (DataSerializationException e) {
                throw new RuntimeException(e);
            }
        }, executor).thenAccept(items -> {
            items.forEach(repository::add);
            logger.info("Loaded {} {} into repository", items.size(), clazz.getSimpleName());
        });
    }

    // Асинхронна фільтрація репозиторію
    public <T> CompletableFuture<List<T>> filterAsync(GenericRepository<T> repository, Predicate<T> condition) {
        return CompletableFuture.supplyAsync(() -> {
            List<T> result = repository.getAll().stream()
                    .filter(condition)
                    .collect(Collectors.toList());
            logger.info("Filtered {} items", result.size());
            return result;
        }, executor);
    }

    // Асинхронне обчислення (наприклад, підрахунок елементів)
    public <T> CompletableFuture<Long> countAsync(GenericRepository<T> repository, Predicate<T> condition) {
        return CompletableFuture.supplyAsync(() -> repository.getAll().stream()
                .filter(condition)
                .count(), executor);
    }

    // Асинхронний пошук першого елемента, що задовольняє умову
    public <T> CompletableFuture<T> findFirstAsync(GenericRepository<T> repository, Predicate<T> condition) {
        return CompletableFuture.supplyAsync(() -> repository.getAll().stream()
                .filter(condition)
                .findFirst()
                .orElse(null), executor);
    }

    // Можна комбінувати результати кількох завдань
    public <T> CompletableFuture<List<T>> combineAsync(
            CompletableFuture<List<T>> task1,
            CompletableFuture<List<T>> task2
    ) {
        return task1.thenCombine(task2, (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        });
    }

    public void shutdown() {
        executor.shutdown();
        logger.info("RepositoryLoader executor has been shut down.");
    }
    public static <T> List<T> loadSync(String filePath, Class<T> clazz, DataSerializer<T> serializer) {
        try {
            logger.info("Synchronously loading {} from file: {}", clazz.getSimpleName(), filePath);
            return serializer.deserialize(filePath, clazz);
        } catch (DataSerializationException e) {
            throw new RuntimeException("Failed to deserialize data from JSON file: " + filePath, e);
        }
    }
}
