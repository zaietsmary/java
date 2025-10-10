package ua.cinema.parser;

import org.junit.jupiter.api.*;
import ua.cinema.exception.InvalidDataException;
import ua.cinema.model.Hall;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HallFileParser Tests")
class HallFileParserTest {

    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("halls_test", ".csv");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    @DisplayName("Parse valid CSV file with halls")
    void testParseValidCSV() throws Exception {
        List<String> lines = List.of(
                "1,100",
                "2,150",
                "3,80"
        );
        Files.write(tempFile, lines);

        List<Hall> halls = HallFileParser.parseFromCSV(tempFile.toString());

        assertEquals(3, halls.size(), "Should parse 3 valid halls");

        assertEquals(1, halls.get(0).getHallNumber());
        assertEquals(100, halls.get(0).getCapacity());
        assertEquals(3, halls.get(2).getHallNumber());
        assertEquals(80, halls.get(2).getCapacity());
    }

    @Test
    @DisplayName("Throw IOException for non-existing file")
    void testFileNotFound() {
        String fakePath = "D:/nonexistent_halls.csv";
        assertThrows(IOException.class, () ->
                        HallFileParser.parseFromCSV(fakePath),
                "Should throw IOException when file not found"
        );
    }

    @Test
    @DisplayName("Throw InvalidDataException for malformed line")
    void testInvalidLineFormat() throws IOException {
        Files.write(tempFile, List.of("invalid_line"));

        InvalidDataException exception = assertThrows(
                InvalidDataException.class,
                () -> HallFileParser.parseHallFromLine("invalid_line"),
                "Should throw exception for malformed line"
        );

        assertTrue(exception.getMessage().contains("Expected format"),
                "Exception message should mention expected format");
    }

    @Test
    @DisplayName("Throw InvalidDataException for non-numeric values")
    void testInvalidNumbers() {
        InvalidDataException exception = assertThrows(
                InvalidDataException.class,
                () -> HallFileParser.parseHallFromLine("one,two"),
                "Should throw exception for invalid numbers"
        );

        assertTrue(exception.getMessage().contains("Invalid number format"),
                "Exception message should mention number format");
    }
}
