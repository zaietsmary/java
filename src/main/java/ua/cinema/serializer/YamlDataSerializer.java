package ua.cinema.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.exception.DataSerializationException; // Додано
import com.fasterxml.jackson.databind.JavaType; // Додано

import java.io.IOException; // Додано
import java.util.List;

/**
 * YAML implementation of DataSerializer using Jackson YAML library
 * @param <T> Type of objects to serialize/deserialize
 */
public class YamlDataSerializer<T> extends AbstractDataSerializer<T> {

    private static final Logger logger = LoggerFactory.getLogger(YamlDataSerializer.class);

    /**
     * Constructor with default ObjectMapper configuration for YAML
     */
    public YamlDataSerializer() {
        super(createDefaultYamlObjectMapper());
        logger.debug("YamlDataSerializer initialized with custom YAML configuration");
    }

    /**
     * Constructor with custom ObjectMapper (for testing or custom configuration)
     * @param objectMapper Custom configured ObjectMapper with YAMLFactory
     */
    public YamlDataSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
        logger.debug("YamlDataSerializer initialized with custom ObjectMapper");
    }

    private static ObjectMapper createDefaultYamlObjectMapper() {
        YAMLFactory yamlFactory = new YAMLFactory()
                // Вимикаємо "---" на початку YAML-документу
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                // Мінімізуємо використання лапок для більш чистого YAML
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);

        ObjectMapper mapper = new ObjectMapper(yamlFactory);
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    // --- РЕАЛІЗАЦІЯ АБСТРАКТНИХ МЕТОДІВ ---

    @Override
    public String getFormat() { // Виправлено: Public, як вимагає DataSerializer
        return "YAML";
    }

    /**
     * Deserialize a single item from String (для POST/PUT запитів)
     */
    @Override
    public T fromString(String data, Class<T> clazz) throws DataSerializationException {
        try {
            return objectMapper.readValue(data, clazz);
        } catch (IOException e) {
            logger.error("Failed to deserialize YAML string into {}: {}", clazz.getSimpleName(), data);
            throw new DataSerializationException("Failed to deserialize YAML string: " + e.getMessage(), e);
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
            logger.error("Failed to serialize item to YAML string: {}", item);
            throw new DataSerializationException("Failed to serialize item to YAML string: " + e.getMessage(), e);
        }
    }

    /**
     * Serialize a list of items to String (для GET запитів)
     */
    @Override
    public String listToString(List<T> items) throws DataSerializationException {
        if (items == null || items.isEmpty()) {
            return ""; // YAML для пустого списку може бути пустим рядком
        }

        try {
            // Jackson може серіалізувати List<T> напряму
            return objectMapper.writeValueAsString(items);
        } catch (IOException e) {
            logger.error("Failed to serialize list to YAML string (size {}): {}", items.size(), e.getMessage());
            throw new DataSerializationException("Failed to serialize list to YAML string", e);
        }
    }
}