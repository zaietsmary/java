package ua.cinema.parser;

import ua.cinema.exception.InvalidDataException;
import ua.cinema.model.Hall;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class HallFileParser {
    private static final Logger logger = Logger.getLogger(HallFileParser.class.getName());

    public static Hall parseHallFromLine(String line) throws InvalidDataException {
        String[] parts = line.split(",");
        if (parts.length != 2) {
            throw new InvalidDataException(
                    "Expected format 'hallNumber,capacity', got: " + line
            );
        }

        try {
            int hallNumber = Integer.parseInt(parts[0].trim());
            int capacity = Integer.parseInt(parts[1].trim());

            return new Hall(hallNumber, capacity);
        } catch (NumberFormatException ex) {
            throw new InvalidDataException(
                    "Invalid number format in line: " + line, ex
            );
        }
    }

    public static List<Hall> parseFromCSV(String filePath) throws IOException, InvalidDataException {
        List<Hall> halls = new ArrayList<>();
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }

        logger.log(Level.INFO, "Starting to parse halls from file: {0}", filePath);

        List<String> lines = Files.readAllLines(path);

        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            try {
                Hall hall = parseHallFromLineWithNumber(line, lineNumber);
                halls.add(hall);
                logger.log(Level.INFO, "Parsed hall from line {0}: {1}",
                        new Object[]{lineNumber, hall.getHallInfo()});
            } catch (InvalidDataException ex) {
                logger.log(Level.WARNING, "Failed to parse line {0}: {1}",
                        new Object[]{lineNumber, ex.getMessage()});
            }
        }
        logger.log(Level.INFO, "Successfully parsed {0} halls from file", halls.size());
        return halls;
    }

    private static Hall parseHallFromLineWithNumber(String line, int lineNumber) throws InvalidDataException {
        try {
            return parseHallFromLine(line);
        } catch (InvalidDataException ex) {
            throw new InvalidDataException("Line " + lineNumber + ": " + ex.getMessage(), ex);
        }
    }
}
