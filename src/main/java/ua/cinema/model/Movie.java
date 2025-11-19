package ua.cinema.model;

import java.time.LocalDate;
import java.util.Comparator;

import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.exception.InvalidDataException;
import ua.cinema.util.ValidationUtils;

public record Movie(
        @NotBlank(message = "title cannot be blank")
        @Size(min = 1, max = 100, message = "title must be between 1 and 100 characters")
        String title,

        @NotNull(message = "genre cannot be null")
        Genre genre,

        @Min(value = 1, message = "duration must be >= 1")
        @Max(value = 500, message = "duration must be <= 500")
        int durationMinutes,

        @NotNull(message = "releaseDate cannot be null")
        LocalDate releaseDate
) implements Comparable<Movie> {

    private static final Logger logger = LoggerFactory.getLogger(Movie.class);

    private static final Comparator<Movie> MOVIE_COMPARATOR =
            Comparator.comparing(Movie::title)
                    .thenComparing(Movie::durationMinutes)
                    .thenComparing(Movie::releaseDate);

    public Movie(String title, Genre genre, int durationMinutes, LocalDate releaseDate) {
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.releaseDate = releaseDate;

        logger.debug("Attempting to validate Movie: title='{}', genre={}, duration={}, releaseDate={}",
                title, genre, durationMinutes, releaseDate);

        try {
            ValidationUtils.validate(this);
            logger.info("Movie successfully created: {}", this);
        } catch (InvalidDataException e) {
            logger.error("Validation failed for Movie: {}", e.getMessage());
            throw e;
        }
    }

    public static Movie createMovie(String title, Genre genre, int durationMinutes, LocalDate releaseDate) {
        logger.debug("Factory: attempting to create Movie: title='{}', genre={}, duration={}, releaseDate={}",
                title, genre, durationMinutes, releaseDate);
        try {
            Movie movie = new Movie(title, genre, durationMinutes, releaseDate);
            logger.info("Factory: created Movie {}", movie);
            return movie;
        } catch (InvalidDataException e) {
            logger.error("Factory: failed to create Movie: {}", e.getMessage());
            throw e;
        }
    }

    public static String getGenreDescription(Genre genre) {
        return switch (genre) {
            case ACTION      -> "Movies with fights and explosions";
            case DRAMA       -> "Movies about life and emotions";
            case COMEDY      -> "Movies for a good mood";
            case HORROR      -> "Movies that scare";
            case DOCUMENTARY -> "Movies about real events";
            case ADVENTURE   -> "Movies about the adventure";
        };
    }

    @Override
    public int compareTo(Movie other) {
        return MOVIE_COMPARATOR.compare(this, other);
    }
}
