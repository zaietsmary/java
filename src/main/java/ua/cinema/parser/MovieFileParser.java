package ua.cinema.parser;

import ua.cinema.exception.InvalidDataException;
import ua.cinema.model.Genre;
import ua.cinema.model.Movie;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovieFileParser {
    private static final Logger logger = Logger.getLogger(MovieFileParser.class.getName());

    public static List<Movie> parseFromCSV(String filePath) throws InvalidDataException {
        List<Movie> movies = new ArrayList<>();

        try {
            logger.log(Level.INFO, "Starting to parse movies from file: {0}", filePath);

            List<String> lines = Files.readAllLines(Path.of(filePath));

            int lineNumber = 0;
            for (String line : lines) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                try {
                    Movie movie = parseMovieFromLine(line, lineNumber);
                    movies.add(movie);
                    logger.log(Level.INFO, "Successfully parsed movie from line {0}: {1}",
                            new Object[]{lineNumber, movie.title()});
                } catch (InvalidDataException e) {
                    logger.log(Level.WARNING, "Failed to parse line {0}: {1}",
                            new Object[]{lineNumber, e.getMessage()});
                }
            }

            logger.log(Level.INFO, "Successfully parsed {0} movies from file", movies.size());
            return movies;

        } catch (IOException e) {
            String errorMsg = "Error reading file: " + filePath;
            logger.log(Level.SEVERE, errorMsg, e);
            throw new InvalidDataException(errorMsg, e);
        }
    }

    private static Movie parseMovieFromLine(String line, int lineNumber) throws InvalidDataException {
        String[] parts = line.split(",");

        if (parts.length != 4) {
            throw new InvalidDataException("Line " + lineNumber +
                    ": Expected format 'title,genre,durationMinutes,releaseDate' but got: " + line);
        }

        String title = parts[0].trim();
        String genreStr = parts[1].trim();
        String durationStr = parts[2].trim();
        String dateStr = parts[3].trim();

        Genre genre;
        try {
            genre = Genre.valueOf(genreStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Line " + lineNumber + ": Invalid genre: " + genreStr, e);
        }

        int durationMinutes;
        try {
            durationMinutes = Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Line " + lineNumber + ": Invalid duration: " + durationStr, e);
        }

        LocalDate releaseDate;
        try {
            releaseDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new InvalidDataException("Line " + lineNumber + ": Invalid release date: " + dateStr, e);
        }

        return new Movie(title, genre, durationMinutes, releaseDate);
    }
}
