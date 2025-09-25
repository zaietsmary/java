package ua.cinema.util;

public class TicketUtils {

    private TicketUtils() {
    }

    public static boolean isValidScreening(Object screening) {
        return screening != null && screening instanceof ua.cinema.model.Screening;
    }

    public static boolean isValidSeatNumber(int seatNumber) {
        return ValidationHelper.isPositiveNumber(seatNumber);
    }

    public static boolean isValidPrice(double price) {
        return ValidationHelper.isPositiveNumber(price);
    }
}
