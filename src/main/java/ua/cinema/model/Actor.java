package ua.cinema.model;

import ua.cinema.util.ActorUtils;

public record Actor(String firstName, String lastName, int birthYear) {

    public Actor {
        if (!ActorUtils.isValidName(firstName)) {
            throw new IllegalArgumentException("Invalid first name: " + firstName);
        }
        if (!ActorUtils.isValidName(lastName)) {
            throw new IllegalArgumentException("Invalid last name: " + lastName);
        }
        if (!ActorUtils.isValidBirthYear(birthYear)) {
            throw new IllegalArgumentException("Invalid birth year: " + birthYear);
        }
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public static Actor of(String firstName, String lastName, int birthYear) {
        return new Actor(firstName, lastName, birthYear);
    }
}
