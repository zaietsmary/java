package ua.cinema;

import ua.cinema.model.Genre;
import ua.cinema.model.Movie;
import ua.cinema.repository.GenericRepository;
import ua.cinema.exception.InvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class MainValidation {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        // Репозиторій для Movie
        GenericRepository<Movie> movieRepository =
                new GenericRepository<>(movie -> movie.title() + "_" + movie.releaseDate(), "Movie");

        // Дані для створення Movie
        Object[][] moviesData = new Object[][]{
                {"Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16)},  // валідний
                {"", Genre.DRAMA, 120, LocalDate.of(2020, 1, 1)},             // невалідний: порожній title
                {"The Godfather", null, 175, LocalDate.of(1972, 3, 24)},      // невалідний: genre null
                {"Interstellar", Genre.DRAMA, 0, LocalDate.of(2014, 11, 7)},  // невалідний: duration 0
                {"The Hangover", Genre.COMEDY, 100, LocalDate.of(2009, 6, 5)} // валідний
        };

        for (Object[] data : moviesData) {
            String title = (String) data[0];
            Genre genre = (Genre) data[1];
            int duration = (int) data[2];
            LocalDate releaseDate = (LocalDate) data[3];

            try {
                // Спроба створити Movie через фабрику
                Movie movie = Movie.createMovie(title, genre, duration, releaseDate);
                logger.info("Attempting to add movie to repository: {}", movie);

                // Додаємо у репозиторій
                boolean added = movieRepository.add(movie);
                if (added) {
                    logger.info("Movie successfully added to repository: {}", movie);
                } else {
                    logger.warn("Movie was not added to repository: {}", movie);
                }

            } catch (InvalidDataException e) {
                // Виводимо повний опис помилок
                logger.error("Failed to create movie: {}", e.getMessage());
            }
        }

        // Підсумок репозиторію
        logger.info("Repository now contains {} valid movies:", movieRepository.size());
        movieRepository.getAll().forEach(m -> logger.info(" - {}", m));
    }
}
