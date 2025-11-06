package ua.cinema.serializer;

import org.junit.jupiter.api.*;
import ua.cinema.exception.DataSerializationException;
import ua.cinema.model.Movie;
import ua.cinema.model.Genre;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonDataSerializerTest {

    private static final String TEST_FILE = "test_movies.json";
    private JsonDataSerializer<Movie> serializer;

    @BeforeEach
    void setUp() {
        serializer = new JsonDataSerializer<>();
    }

    @AfterEach
    void cleanUp() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    @DisplayName("Серіалізація і десеріалізація повинні зберігати ті ж самі дані")
    void testSerializeAndDeserialize() throws DataSerializationException {
        List<Movie> originalMovies = List.of(
                new Movie("Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16)),
                new Movie("Oppenheimer", Genre.DRAMA, 180, LocalDate.of(2023, 7, 21))
        );

        serializer.serialize(originalMovies, TEST_FILE);
        List<Movie> deserializedMovies = serializer.deserialize(TEST_FILE, Movie.class);

        assertEquals(originalMovies, deserializedMovies, "Дані після десеріалізації не збігаються!");
    }

    @Test
    @DisplayName("Повинно викидати виняток при читанні неіснуючого файлу")
    void testDeserializeInvalidFile() {
        assertThrows(DataSerializationException.class, () ->
                serializer.deserialize("nonexistent_file.json", Movie.class)
        );
    }

    @Test
    @DisplayName("Повинно викидати виняток при спробі серіалізувати null")
    void testSerializeNull() {
        assertThrows(DataSerializationException.class, () ->
                serializer.serialize(null, TEST_FILE)
        );
    }
}
