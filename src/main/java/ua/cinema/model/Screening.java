package ua.cinema.model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import ua.cinema.exception.InvalidDataException;
import ua.cinema.util.ValidationUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Screening implements Comparable<Screening> {

    private static final Logger logger = LoggerFactory.getLogger(Screening.class);

    @NotNull(message = "movie cannot be null")
    private Movie movie;

    @NotNull(message = "hall cannot be null")
    private Hall hall;

    @NotNull(message = "screeningDateTime cannot be null")
    private LocalDate screeningDateTime;

    public static final Comparator<Screening> SCREENING_COMPARATOR =
            Comparator.comparing(Screening::getMovie)
                    .thenComparing(Screening::getHall)
                    .thenComparing(Screening::getScreeningDateTime);


    public Screening(@JsonProperty("movie") Movie movie,
                     @JsonProperty("hall") Hall hall,
                     @JsonProperty("screeningDateTime") LocalDate screeningDateTime) {
        this.movie = movie;
        this.hall = hall;
        this.screeningDateTime = screeningDateTime;
        try {
            ValidationUtils.validate(this);
            logger.info("Screening successfully created: {}", this);
        } catch (InvalidDataException e) {
            logger.error("Failed to create Screening: {}", e.getMessage());
            throw e;
        }
    }

    public static Screening createScreening(Movie movie, Hall hall, LocalDate screeningDateTime) {
        logger.debug(
                "Factory: attempting to create Screening: movie={}, hall={}, screeningDateTime={}",
                movie, hall, screeningDateTime
        );

        try {
            Screening screening = new Screening(movie, hall, screeningDateTime);
            ValidationUtils.validate(screening);
            logger.info("Factory: created Screening {}", screening);
            return screening;
        } catch (InvalidDataException e) {
            logger.error("Factory: failed to create Screening: {}", e.getMessage());
            throw e;
        }
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        Movie old = this.movie;
        this.movie = movie;
        try {
            ValidationUtils.validate(this);
            logger.info("Updated movie for Screening: {}", this);
        } catch (InvalidDataException e) {
            this.movie = old;
            logger.error("Invalid movie for Screening, rollback to previous: {}", old, e);
            throw e;
        }
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        Hall old = this.hall;
        this.hall = hall;
        try {
            ValidationUtils.validate(this);
            logger.info("Updated hall for Screening: {}", this);
        } catch (InvalidDataException e) {
            this.hall = old;
            logger.error("Invalid hall for Screening, rollback to previous: {}", old, e);
            throw e;
        }
    }

    public LocalDate getScreeningDateTime() {
        return screeningDateTime;
    }

    public void setScreeningDateTime(LocalDate screeningDateTime) {
        LocalDate old = this.screeningDateTime;
        this.screeningDateTime = screeningDateTime;
        try {
            ValidationUtils.validate(this);
            logger.info("Updated screeningDateTime for Screening: {}", this);
        } catch (InvalidDataException e) {
            this.screeningDateTime = old;
            logger.error("Invalid screeningDateTime for Screening, rollback to previous: {}", old, e);
            throw e;
        }
    }

    @Override
    public int compareTo(@NotNull Screening other) {
        return SCREENING_COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return "Screening{" +
                "movie=" + movie +
                ", hall=" + hall +
                ", screeningDateTime=" + screeningDateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Screening)) return false;
        Screening that = (Screening) o;
        return movie.equals(that.movie) &&
                hall.equals(that.hall) &&
                screeningDateTime.equals(that.screeningDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movie, hall, screeningDateTime);
    }
}
