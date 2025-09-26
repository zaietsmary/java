package ua.cinema.util;

import ua.cinema.model.Genre;
import java.time.LocalDate;

public class MovieUtils {

    private MovieUtils() {}

    public static boolean isValidTitle(String title) {
        return ValidationHelper.isStringLengthBetween(title, 1, 30);
    }

    public static boolean isValidDuration(int duration) {
        return ValidationHelper.isPositiveNumber(duration);
    }

    public static boolean isValidGenre(Genre genre) { return genre != null; }

    public static boolean isValidReleaseDate(LocalDate releaseDate) {
        if (releaseDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate hundredYearsAgo = today.minusYears(100);
        return !releaseDate.isAfter(today) && !releaseDate.isBefore(hundredYearsAgo);
    }
}
