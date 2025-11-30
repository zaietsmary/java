package ua.cinema.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.exception.DataSerializationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for data serializers with common validation and file handling logic.
 * Implements logic that is format-independent (like file path validation and list serialization/deserialization to files).
 * * @param <T> Type of objects to serialize/deserialize
 */
public abstract class AbstractDataSerializer<T> implements DataSerializer<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataSerializer.class);
    protected final ObjectMapper objectMapper;

    protected AbstractDataSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // --- АБСТРАКТНІ МЕТОДИ ДЛЯ КОНКРЕТНОЇ РЕАЛІЗАЦІЇ ---

    /**
     * Get the format name (e.g., "JSON", "YAML").
     * Must be implemented by concrete subclasses.
     */
    @Override
    public abstract String getFormat();

    /**
     * Serialize a single item to String (required by DataSerializer).
     * Must be implemented by concrete subclasses (e.g., JsonDataSerializer).
     */
    @Override
    public abstract String toString(T item) throws DataSerializationException;

    /**
     * Serialize a list of items to String (required by DataSerializer).
     * Must be implemented by concrete subclasses (e.g., JsonDataSerializer).
     */
    @Override
    public abstract String listToString(List<T> items) throws DataSerializationException;

    /**
     * Deserialize a single item from String (required by DataSerializer).
     * Must be implemented by concrete subclasses (e.g., JsonDataSerializer).
     */
    @Override
    public abstract T fromString(String str, Class<T> clazz) throws DataSerializationException;

    // --- ФУНКЦІОНАЛ ДЛЯ РОБОТИ З ФАЙЛАМИ (РЕАЛІЗОВАНО ТУТ) ---

    @Override
    public void serialize(List<T> items, String filePath) throws DataSerializationException {
        validateItemsForSerialization(items);
        validateFilePath(filePath);

        try {
            File file = new File(filePath);
            createParentDirectories(file);

            objectMapper.writeValue(file, items);
            logger.info("Successfully serialized {} items to {} file: {}",
                    items.size(), getFormat(), filePath);

        } catch (IOException e) {
            String errorMsg = String.format("Failed to serialize data to %s file: %s",
                    getFormat(), filePath);
            throw new DataSerializationException(errorMsg, e);
        }
    }

    @Override
    public List<T> deserialize(String filePath, Class<T> clazz) throws DataSerializationException {
        validateFilePath(filePath);
        validateClass(clazz);

        try {
            File file = new File(filePath);

            if (!file.exists()) {
                throw new DataSerializationException("File does not exist: " + filePath);
            }

            if (file.length() == 0) {
                logger.warn("File is empty: {}. Returning empty list.", filePath);
                return new ArrayList<>(); // Повертаємо порожній список, якщо файл пустий
            }

            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            List<T> items = objectMapper.readValue(file, type);

            if (items == null) {
                items = new ArrayList<>();
            }

            logger.info("Successfully deserialized {} items from {} file: {}",
                    items.size(), getFormat(), filePath);
            return items;

        } catch (IOException e) {
            String errorMsg = String.format("Failed to deserialize data from %s file: %s",
                    getFormat(), filePath);
            throw new DataSerializationException(errorMsg, e);
        }
    }

    // --- ДОПОМІЖНІ МЕТОДИ ---

    protected void validateItemsForSerialization(List<T> items) throws DataSerializationException {
        if (items == null) {
            throw new DataSerializationException("Cannot serialize null list");
        }
    }

    protected void validateFilePath(String filePath) throws DataSerializationException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new DataSerializationException("File path cannot be null or empty");
        }
    }

    protected void validateClass(Class<T> clazz) throws DataSerializationException {
        if (clazz == null) {
            throw new DataSerializationException("Class type cannot be null");
        }
    }

    protected void createParentDirectories(File file) {
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (created) {
                logger.info("Created directory: {}", parentDir.getAbsolutePath());
            }
        }
    }
}