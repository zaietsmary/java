package ua.cinema.repository;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.exception.AlreadyExistsException;
import ua.cinema.util.ValidationUtils;
import ua.cinema.exception.InvalidDataException;


public class GenericRepository<T> {
    private static final Logger logger = LoggerFactory.getLogger(GenericRepository.class);

    private final List<T> items;
    private final IdentityExtractor<T> identityExtractor;
    private final String entityType;

    public GenericRepository(IdentityExtractor<T> identityExtractor, String entityType) {
        this.items = new CopyOnWriteArrayList<>();
        this.identityExtractor = identityExtractor;
        this.entityType = entityType;
        logger.info("Created repository for {}", entityType);
    }


    public synchronized boolean add(T item) {
        if (item == null) {
            throw new InvalidDataException(entityType + " cannot be null");
        }

        String identity = identityExtractor.extractIdentity(item);

        if (findByIdentity(identity).isPresent()) {
            String errorMsg = String.format("%s already exists with identity: %s", entityType, identity);
            logger.error(errorMsg);
            throw new AlreadyExistsException(errorMsg);
        }

        items.add(item);
        logger.info("Added {}: {}", entityType, identity);
        return true;
    }

    public int addAll(Collection<T> newItems) {
        if (newItems == null || newItems.isEmpty()) {
            return 0;
        }

        int addedCount = 0;
        for (T item : newItems) {
            try {
                if (add(item)) {
                    addedCount++;
                }
            } catch (AlreadyExistsException e) {
                logger.debug("Skipping duplicate: {}", e.getMessage());
            }
        }

        logger.info("Bulk added {} of {} items to {}", addedCount, newItems.size(), entityType);
        return addedCount;
    }

    /**
     * Remove an item using equals() method - RECOMMENDED APPROACH
     */
    public boolean remove(T item) {
        if (item == null) {
            logger.warn("Attempted to remove null {}", entityType);
            return false;
        }

        boolean removed = items.remove(item); // Uses equals() internally
        if (removed) {
            logger.info("Removed {}: {}", entityType, identityExtractor.extractIdentity(item));
        } else {
            logger.warn("Failed to remove {}: {}", entityType, identityExtractor.extractIdentity(item));
        }
        return removed;
    }

    /**
     * Remove by identity - alternative approach when you only have the identity
     */
    public boolean removeByIdentity(String identity) {
        if (identity == null) {
            logger.warn("Attempted to remove {} with null identity", entityType);
            return false;
        }

        Optional<T> itemToRemove = items.stream()
                .filter(item -> identity.equals(identityExtractor.extractIdentity(item)))
                .findFirst();

        if (itemToRemove.isPresent()) {
            boolean removed = items.remove(itemToRemove.get());
            if (removed) {
                logger.info("Removed {} by identity: {}", entityType, identity);
            }
            return removed;
        } else {
            logger.warn("No {} found with identity: {} to remove", entityType, identity);
            return false;
        }
    }

    /**
     * Check if repository contains an item using equals()
     */
    public boolean contains(T item) {
        return items.contains(item); // Uses equals() internally
    }

    /**
     * Check if repository contains an item with given identity
     */
    public boolean containsIdentity(String identity) {
        return findByIdentity(identity).isPresent();
    }

    /**
     * Find an object by its unique identity field
     */
    public Optional<T> findByIdentity(String identity) {
        if (identity == null) {
            logger.warn("Attempted to find {} with null identity", entityType);
            return Optional.empty();
        }

        Optional<T> result = items.stream()
                .filter(item -> identity.equals(identityExtractor.extractIdentity(item)))
                .findFirst();

        if (result.isPresent()) {
            logger.info("Found {} with identity: {}", entityType, identity);
        } else {
            logger.info("No {} found with identity: {}", entityType, identity);
        }

        return result;
    }

    public List<T> getAll() {
        logger.info("Retrieved all {}  items. Count: {}", entityType, items.size());
        return new ArrayList<>(items);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        int sizeBefore = items.size();
        items.clear();
        logger.info("Cleared repository. Removed {} {} items",  sizeBefore,  entityType);
    }

    /**
     * Package-private method for testing - allows direct access to items
     * This should ONLY be used by tests in the same package
     */
    List<T> getItemsForTesting() {
        return items;
    }

    /**
     * Sort items by identity using ascending or descending order.
     */
    public void sortByIdentity(boolean asc) {
        items.sort(Comparator.comparing(identityExtractor::extractIdentity));
        if (!asc) {
            Collections.reverse(items);
        }
        logger.info("Sorted {} by identity in {} order", entityType, asc ? "ascending" : "descending");
    }

    /**
     * Alternative: sort using String order ("desc" for descending, any other value for ascending)
     */
    public void sortByIdentity(String order) {
        boolean asc = !"desc".equalsIgnoreCase(order);
        logger.debug("sortByIdentity called with order: '{}', interpreted as: {}", order, asc ? "ascending" : "descending");
        sortByIdentity(asc);
    }

}