package ua.cinema.util;

public class HallUtils {


    private HallUtils() {}

    public static boolean isValidHallNumber(int hallNumber) {
        return ValidationHelper.isPositiveNumber(hallNumber);
    }

    public static boolean isValidCapacity(int capacity) {
        return ValidationHelper.isPositiveNumber(capacity);
    }

}
