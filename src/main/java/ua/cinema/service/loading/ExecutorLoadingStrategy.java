package ua.cinema.service.loading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.exception.DataSerializationException;
import ua.cinema.model.*;
import ua.cinema.repository.*;
import ua.cinema.service.LoadResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorLoadingStrategy implements LoadingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorLoadingStrategy.class);

    private final int threadPoolSize;

    public ExecutorLoadingStrategy(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public ExecutorLoadingStrategy() {
        this(4);
    }

    @Override
    public LoadResult load(
            TicketRepository ticketRepository,
            ActorRepository actorRepository,
            DataLoader dataLoader) {

        logger.info("Starting loading with ExecutorService (pool size: {})...", threadPoolSize);
        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        try {
            CompletableFuture<Integer> actorsFuture = CompletableFuture
                    .supplyAsync(() -> loadEntity(dataLoader, Actor.class, actorRepository), executor);

            CompletableFuture<Integer> teachersFuture = CompletableFuture
                    .supplyAsync(() -> loadEntity(dataLoader, Ticket.class, ticketRepository), executor);

            CompletableFuture.allOf(actorsFuture, teachersFuture).join();

            long duration = System.currentTimeMillis() - startTime;
            logger.info("ExecutorService loading completed in {} ms", duration);

            return new LoadResult(
                    actorsFuture.join(),
                    teachersFuture.join(),
                    duration
            );
        } finally {
            shutdownExecutor(executor);
        }
    }

    private <T> int loadEntity(DataLoader dataLoader, Class<T> clazz, GenericRepository<T> repository) {
        try {
            return dataLoader.loadEntity(clazz, repository);
        } catch (DataSerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}