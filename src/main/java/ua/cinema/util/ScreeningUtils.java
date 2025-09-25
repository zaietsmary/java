package ua.cinema.util;

import java.time.LocalDate;

public class ScreeningUtils {

    private ScreeningUtils() {
    }

    public static boolean isValidScreeningDateTime(LocalDate screeningDateTime) {
        return screeningDateTime != null && screeningDateTime.isAfter(LocalDate.now());
    }

    public static boolean isValidMovie(Object movie) {
        return movie != null && movie instanceof ua.cinema.model.Movie;
    }

    public static boolean isValidHall(Object hall) {
        return hall != null && hall instanceof ua.cinema.model.Hall;
    }
}
