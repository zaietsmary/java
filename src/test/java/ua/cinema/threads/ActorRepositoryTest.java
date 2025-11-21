package ua.cinema.threads;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.cinema.model.Actor;
import ua.cinema.repository.ActorRepository;
import ua.cinema.serializer.JsonDataSerializer;
import ua.cinema.service.RepositoryLoader;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ActorRepositoryTest {

    @Test
    public void testFilteringParallelStream() throws Exception {
        ActorRepository repo = loadTestRepo();

        List<Actor> result = repo.getAll()
                .parallelStream()
                .filter(a -> a.getBirthYear() > 1980)
                .collect(Collectors.toList());

        Assertions.assertEquals(15, result.size());
    }

    @Test
    public void testFilteringExecutorService() throws Exception {
        ActorRepository repo = loadTestRepo();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<List<Actor>> future = executor.submit(() ->
                repo.getAll().stream()
                        .filter(a -> a.getBirthYear() > 1980)
                        .collect(Collectors.toList())
        );
        List<Actor> result = future.get();
        executor.shutdown();

        Assertions.assertEquals(15, result.size());
    }

    @Test
    public void testFilteringCompletableFuture() throws Exception {
        ActorRepository repo = loadTestRepo();

        CompletableFuture<List<Actor>> cf = CompletableFuture.supplyAsync(() ->
                repo.getAll().stream()
                        .filter(a -> a.getBirthYear() > 1980)
                        .collect(Collectors.toList())
        );

        List<Actor> result = cf.get();

        Assertions.assertEquals(15, result.size());
    }

    @Test
    public void testSorting() throws Exception {
        ActorRepository repo = loadTestRepo();

        List<Actor> sorted = repo.sortByName();

        Assertions.assertEquals("Cavill", sorted.get(0).getLastName());
        Assertions.assertEquals("Winslet", sorted.get(sorted.size() - 1).getLastName());
    }


    /** Helper: load repository from actors.json */
    private ActorRepository loadTestRepo() throws Exception {
        ActorRepository repo = new ActorRepository();
        List<Actor> actors = RepositoryLoader.loadSync("data/actors.json",
                Actor.class, new JsonDataSerializer<>());
        repo.addAll(actors);
        return repo;
    }
}
