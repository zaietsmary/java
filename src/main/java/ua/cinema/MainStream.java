package ua.cinema;

import ua.cinema.model.Actor;
import ua.cinema.repository.ActorRepository;
import ua.cinema.serializer.JsonDataSerializer;
import ua.cinema.service.RepositoryLoader;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MainStream {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Cinema Actor Processing ===");

        ActorRepository repo = new ActorRepository();
        System.out.println("Repository created.");

        // Load actors from JSON
        System.out.println("Loading actors from file: data/actors.json");
        List<Actor> actors = RepositoryLoader.loadSync("data/actors.json", Actor.class, new JsonDataSerializer<>());
        int addedCount = repo.addAll(actors);
        System.out.println("✅ Loaded actors: " + addedCount);

        System.out.println("\n--- All actors in repository ---");
        repo.getAll().forEach(System.out::println);

        // --- ParallelStream ---
        System.out.println("\n--- Filtering actors born after 1980 (ParallelStream) ---");
        long startTotal = System.currentTimeMillis();
        List<Actor> bornAfter1980Parallel = repo.getAll().parallelStream()
                .peek(a -> {
                    long start = System.currentTimeMillis();
                    try {
                        Thread.sleep(1000); // sleep 1 секунда
                    } catch (InterruptedException ignored) {}
                    long end = System.currentTimeMillis();
                    System.out.println(Thread.currentThread().getName() + " processed " + a.getFirstName() +
                            " in " + (end - start) + " ms");
                })
                .filter(a -> a.getBirthYear() > 1980)
                .collect(Collectors.toList());
        long endTotal = System.currentTimeMillis();
        System.out.println("ParallelStream found " + bornAfter1980Parallel.size() +
                " actors in " + (endTotal - startTotal) + " ms");

        // --- ExecutorService + Callable ---
        System.out.println("\n--- Filtering actors born after 1980 (ExecutorService) ---");
        ExecutorService executor = Executors.newFixedThreadPool(4);
        startTotal = System.currentTimeMillis();
        Future<List<Actor>> future = executor.submit(() -> {
            return repo.getAll().stream()
                    .peek(a -> {
                        long start = System.currentTimeMillis();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {}
                        long end = System.currentTimeMillis();
                        System.out.println(Thread.currentThread().getName() + " processed " + a.getFirstName() +
                                " in " + (end - start) + " ms");
                    })
                    .filter(a -> a.getBirthYear() > 1980)
                    .collect(Collectors.toList());
        });
        List<Actor> bornAfter1980ES = future.get();
        endTotal = System.currentTimeMillis();
        System.out.println("ExecutorService found " + bornAfter1980ES.size() +
                " actors in " + (endTotal - startTotal) + " ms");

        // --- CompletableFuture + Supplier ---
        System.out.println("\n--- Filtering actors born after 1980 (CompletableFuture) ---");
        startTotal = System.currentTimeMillis();
        CompletableFuture<List<Actor>> cf = CompletableFuture.supplyAsync(() -> {
            return repo.getAll().stream()
                    .peek(a -> {
                        long start = System.currentTimeMillis();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {}
                        long end = System.currentTimeMillis();
                        System.out.println(Thread.currentThread().getName() + " processed " + a.getFirstName() +
                                " in " + (end - start) + " ms");
                    })
                    .filter(a -> a.getBirthYear() > 1980)
                    .collect(Collectors.toList());
        });
        List<Actor> bornAfter1980CF = cf.get();
        endTotal = System.currentTimeMillis();
        System.out.println("CompletableFuture found " + bornAfter1980CF.size() +
                " actors in " + (endTotal - startTotal) + " ms");

        executor.shutdown();

        System.out.println("\n=== All tasks completed ===");
    }
}
