package ua.cinema;

import ua.cinema.model.Genre;
import ua.cinema.model.Movie;
import ua.cinema.repository.GenericRepository;
import ua.cinema.exception.InvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class MainValidation {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        GenericRepository<Movie> movieRepository =
                new GenericRepository<>(movie -> movie.title() + "_" + movie.releaseDate(), "Movie");

        List<Movie> moviesData = List.of(
                new Movie("Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16)),
                new Movie("", Genre.DRAMA, 120, LocalDate.of(2020, 1, 1)),
                new Movie("The Godfather", null, 175, LocalDate.of(1972, 3, 24)),
                new Movie("Interstellar", Genre.DRAMA, 0, LocalDate.of(2014, 11, 7)),
                new Movie("The Hangover", Genre.COMEDY, 100, LocalDate.of(2009, 6, 5))
        );

        for (Movie row : moviesData) {

            try {
                Movie movie = Movie.createMovie(
                        row.title(),
                        row.genre(),
                        row.durationMinutes(),
                        row.releaseDate()
                );

                logger.info("Attempting to add movie to repository: {}", movie);

                if (movieRepository.add(movie)) {
                    logger.info("Movie successfully added to repository: {}", movie);
                } else {
                    logger.warn("Movie was not added to repository: {}", movie);
                }

            } catch (InvalidDataException e) {
                logger.error("Failed to create movie: {}", e.getMessage());
            }
        }

        logger.info("Repository now contains {} valid movies:", movieRepository.size());
        movieRepository.getAll().forEach(m -> logger.info(" - {}", m));
    }
}
