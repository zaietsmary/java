package ua.cinema.model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import ua.cinema.util.ScreeningUtils;

public class Screening implements Comparable<Screening> {
    private Movie movie;
    private Hall hall;
    private LocalDate screeningDateTime;

    public static final Comparator<Screening> SCREENING_COMPARATOR =
            Comparator.comparing((Screening s) -> s.getMovie())
            .thenComparing((Screening s) -> s.getHall())
            .thenComparing((Screening s) -> s.getScreeningDateTime());

    public Screening() {
    }

    public Screening(Movie movie, Hall hall, LocalDate screeningDateTime) {
        if(!ScreeningUtils.isValidScreeningDateTime(screeningDateTime)){
            throw new IllegalArgumentException("Invalid screening date and time: " + screeningDateTime);
        }
        if(!ScreeningUtils.isValidMovie(movie)){
            throw new IllegalArgumentException("Invalid movie: " + movie);
        }
        if(!ScreeningUtils.isValidHall(hall)){
            throw new IllegalArgumentException("Invalid hall: " + hall);
        }
        this.movie = movie;
        this.hall = hall;
        this.screeningDateTime = screeningDateTime;
    }

    public Hall getHall() {
        if(!ScreeningUtils.isValidHall(hall)){
            throw new IllegalArgumentException("Invalid hall: " + hall);
        }
        return hall;
    }

    public void setHall(Hall hall) {
        if(!ScreeningUtils.isValidHall(hall)){
            throw new IllegalArgumentException("Invalid hall: " + hall);
        }
        this.hall = hall;
    }

    public Movie getMovie() {
        if(!ScreeningUtils.isValidMovie(movie)){
            throw new IllegalArgumentException("Invalid movie: " + movie);
        }
        return movie;
    }

    public void setMovie(Movie movie) {
        if(!ScreeningUtils.isValidMovie(movie)){
            throw new IllegalArgumentException("Invalid movie: " + movie);
        }
        this.movie = movie;
    }

    public LocalDate getScreeningDateTime() {
        if(!ScreeningUtils.isValidScreeningDateTime(screeningDateTime)){
            throw new IllegalArgumentException("Invalid screening date and time: " + screeningDateTime);
        }
        return screeningDateTime;
    }

    public void setScreeningDateTime(LocalDate screeningDateTime) {
        if(!ScreeningUtils.isValidScreeningDateTime(screeningDateTime)){
            throw new IllegalArgumentException("Invalid screening date and time: " + screeningDateTime);
        }
        this.screeningDateTime = screeningDateTime;
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
        if (o == null || getClass() != o.getClass()) return false;
        Screening screening = (Screening) o;
        return screeningDateTime == screening.screeningDateTime &&
                movie.equals(screening.movie) &&
                hall.equals(screening.hall);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movie, hall, screeningDateTime);
    }

    public static Screening of(Movie movie, Hall hall, LocalDate screeningDateTime) {
        return new Screening(movie, hall, screeningDateTime);
    }

    @Override
    public int compareTo(@NotNull Screening other) {
        return SCREENING_COMPARATOR.compare(this, other);
    }
}
