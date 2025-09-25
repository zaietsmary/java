package ua.cinema.model;

import java.util.Objects;

import ua.cinema.util.ActorUtils;

public class Actor {
    private String firstName;
    private String lastName;
    private int birthYear;

    public Actor() {
    }

    public Actor(String firstName, String lastName, int birthYear) {
        if (!ActorUtils.isValidName(firstName)) {
            throw new IllegalArgumentException("Invalid first name: " + firstName);
        }
        if (!ActorUtils.isValidName(lastName)) {
            throw new IllegalArgumentException("Invalid last name: " + lastName);
        }
        if (!ActorUtils.isValidBirthYear(birthYear)) {
            throw new IllegalArgumentException("Invalid birth year: " + birthYear);
        }
        this.firstName = formatName(firstName);
        this.lastName = formatName(lastName);
        this.birthYear = birthYear;
    }

    public String getFirstName() {
        if(!ActorUtils.isValidName(firstName)) {
            throw new IllegalArgumentException("Invalid first name: " + firstName);
        }
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (!ActorUtils.isValidName(firstName)) {
            throw new IllegalArgumentException("Invalid first name: " + firstName);
        }
        this.firstName = formatName(firstName);
    }

    public String getLastName() {
        if(!ActorUtils.isValidName(lastName)) {
            throw new IllegalArgumentException("Invalid last name: " + lastName);
        }
        return lastName;
    }

    public void setLastName(String lastName) {
        if (!ActorUtils.isValidName(lastName)) {
            throw new IllegalArgumentException("Invalid last name: " + lastName);
        }
        this.lastName = formatName(lastName);
    }

    public int getBirthYear() {
        if(!ActorUtils.isValidBirthYear(birthYear)) {
            throw new IllegalArgumentException("Invalid birth year: " + birthYear);
        }
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        if (!ActorUtils.isValidBirthYear(birthYear)) {
            throw new IllegalArgumentException("Invalid birth year: " + birthYear);
        }
        this.birthYear = birthYear;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthYear=" + birthYear +
                '}';
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public static String formatName(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        if (name.isEmpty()) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Actor actor = (Actor) o;
        return birthYear == actor.birthYear &&
                firstName.equals(actor.firstName) &&
                lastName.equals(actor.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, birthYear);
    }

    public static Actor of(String firstName, String lastName, int birthYear) {
        return new Actor(firstName, lastName, birthYear);
    }
}
