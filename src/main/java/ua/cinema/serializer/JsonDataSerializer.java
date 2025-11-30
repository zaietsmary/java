package ua.cinema.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.JavaType; // Додано
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.exception.DataSerializationException; // Додано

import java.io.IOException; // Додано
import java.util.List;
import java.util.ArrayList; // Додано

/**
 * JSON implementation of DataSerializer using Jackson library
 * @param <T> Type of objects to serialize/deserialize
 */
public class JsonDataSerializer<T> extends AbstractDataSerializer<T> {

    private static final Logger logger = LoggerFactory.getLogger(JsonDataSerializer.class);

    /**
     * Constructor with default ObjectMapper configuration
     */
    public JsonDataSerializer() {
        super(createDefaultObjectMapper());
        logger.debug("JsonDataSerializer initialized with pretty printing enabled");
    }

    /**
     * Constructor with custom ObjectMapper (for testing or custom configuration)
     * @param objectMapper Custom configured ObjectMapper
     */
    public JsonDataSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
        logger.debug("JsonDataSerializer initialized with custom ObjectMapper");
    }

    private static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Для підтримки Java 8 Date/Time API
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Гарне форматування JSON
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Використовувати ISO-8601
        return mapper;
    }

    @Override
    public String getFormat() {
        return "JSON";
    }

    // --- РЕАЛІЗАЦІЯ АБСТРАКТНИХ МЕТОДІВ З ABSTRACTDATASERIALIZER ---

    /**
     * Deserialize a single item from String (для POST/PUT запитів)
     */
    @Override
    public T fromString(String data, Class<T> clazz) throws DataSerializationException {
        try {
            // Використовуємо ObjectMapper, успадкований від AbstractDataSerializer
            return objectMapper.readValue(data, clazz);
        } catch (IOException e) {
            logger.error("Failed to deserialize JSON string into {}: {}", clazz.getSimpleName(), data);
            throw new DataSerializationException("Failed to deserialize JSON string: " + e.getMessage(), e);
        }
    }

    /**
     * Serialize a single item to String (для GET запитів)
     */
    @Override
    public String toString(T item) throws DataSerializationException {
        try {
            return objectMapper.writeValueAsString(item);
        } catch (IOException e) {
            logger.error("Failed to serialize item to JSON string: {}", item);
            throw new DataSerializationException("Failed to serialize item to JSON string: " + e.getMessage(), e);
        }
    }

    /**
     * Serialize a list of items to String (для GET запитів)
     */
    @Override
    public String listToString(List<T> items) throws DataSerializationException {
        if (items == null || items.isEmpty()) {
            return "[]";
        }

        try {
            // Jackson може серіалізувати List<T> напряму
            return objectMapper.writeValueAsString(items);
        } catch (IOException e) {
            logger.error("Failed to serialize list to JSON string (size {}): {}", items.size(), e.getMessage());
            throw new DataSerializationException("Failed to serialize list to JSON string", e);
        }
    }
}