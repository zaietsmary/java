package ua.cinema.util;

import java.time.LocalDate;

public class ActorUtils {
    private ActorUtils() {}

    public static boolean isValidName(String name){
        return ValidationHelper.isStringLengthBetween(name, 1, 20)
                && ValidationHelper.isStringMatchPattern(name, "^[A-ZА-Я][a-zа-яA-ZА-Я'-]*$");
    }

    public static boolean isValidBirthYear(int year){
        return ValidationHelper.isNumberBetween (year,LocalDate.now().getYear()-100, LocalDate.now().getYear());
    }
}
