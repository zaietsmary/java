package ua.cinema.service.loading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.exception.DataSerializationException;
import ua.cinema.persistence.PersistenceManager;
import ua.cinema.repository.*;
import ua.cinema.service.LoadResult;

import java.util.List;

/**
 * Main service for loading data using different strategies
 */
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final PersistenceManager persistenceManager;
    private final String format;

    public DataLoader(PersistenceManager persistenceManager, String format) {
        this.persistenceManager = persistenceManager;
        this.format = format;
        logger.info("DataLoader initialized with format: {}", format);
    }

    public DataLoader(PersistenceManager persistenceManager) {
        this(persistenceManager, "JSON");
    }

    /**
     * Load entities using class name as entity type
     * Student.class -> "students"
     */
    public <T> int loadEntity(Class<T> clazz, GenericRepository<T> repository)
            throws DataSerializationException {
        String entityType = clazz.getSimpleName().toLowerCase() + "s";
        return loadEntity(entityType, clazz, repository);
    }

    /**
     * Load entities with custom entity type name
     */
    public <T> int loadEntity(String entityType, Class<T> clazz, GenericRepository<T> repository)
            throws DataSerializationException {
        List<T> items = persistenceManager.load(entityType, clazz, format);
        return repository.addAll(items);
    }

    public LoadResult load(
            ActorRepository actorRepository,
            TicketRepository ticketRepository,
            LoadingStrategy strategy) {

        logger.info("Loading data using strategy: {}", strategy.getClass().getSimpleName());

        return strategy.load(
                ticketRepository,
                actorRepository,
                this
        );
    }
}