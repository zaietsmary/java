package ua.cinema.util;

import java.util.regex.Pattern;

public class ValidationHelper {

    private ValidationHelper() {
    }

    public static boolean isStringMatchPattern(String text, String pattern) {
        if (text == null || pattern == null) {
            return false;
        }
        return Pattern.matches(pattern, text);
    }

    public static boolean isNumberBetween(int number, int min, int max) {
        return number >= min && number <= max;
    }

    public static boolean isStringLengthBetween(String text, int min, int max) {
        if (text == null) {
            return false;
        }
        int length = text.trim().length();
        return length >= min && length <= max;
    }

    public static boolean isPositiveNumber(int number) {
        return number > 0;
    }

    public static boolean isPositiveNumber(double number) {
        return number > 0;
    }


}
