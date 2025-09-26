package ua.cinema.model;

import java.time.LocalDate;

import ua.cinema.util.ScreeningUtils;

public record Screening(Movie movie, Hall hall, LocalDate screeningDateTime) {

    public Screening {
        if(!ScreeningUtils.isValidScreeningDateTime(screeningDateTime)){
            throw new IllegalArgumentException("Invalid screening date and time: " + screeningDateTime);
        }
        if(!ScreeningUtils.isValidMovie(movie)){
            throw new IllegalArgumentException("Invalid movie: " + movie);
        }
        if(!ScreeningUtils.isValidHall(hall)){
            throw new IllegalArgumentException("Invalid hall: " + hall);
        }
    }

    public static Screening of(Movie movie, Hall hall, LocalDate screeningDateTime) {
        return new Screening(movie, hall, screeningDateTime);
    }
}
