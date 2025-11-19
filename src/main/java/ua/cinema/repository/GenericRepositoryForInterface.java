package ua.cinema.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.exception.InvalidDataException;
import ua.cinema.util.ValidationUtils;

public class GenericRepositoryForInterface<T extends Identity> {
    private static final Logger logger = LoggerFactory.getLogger(GenericRepository.class);

    private final List<T> items;
    private final String entityType;

    public GenericRepositoryForInterface(String entityType) {
        items = new ArrayList<>();
        this.entityType = entityType;
    }

    public boolean add(T item) {
        if (item == null) return false;

        try {
            ValidationUtils.validate(item);
        } catch (InvalidDataException e) {
            System.err.println("Cannot add invalid " + entityType + ": " + e.getMessage());
            return false;
        }

        if (findByIdentity(item.getIdentity()).isPresent()) {
            return false;
        }

        return items.add(item);
    }

    public Optional<T> findByIdentity(String identity) {
        return items.stream()
                .filter(i -> i.getIdentity().equals(identity))
                .findFirst();
    }

    public boolean containsIdentity(String identity) {
        return findByIdentity(identity).isPresent();
    }

    // other methods...
}