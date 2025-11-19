package ua.cinema.model;

import java.util.Comparator;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.*;
import ua.cinema.exception.InvalidDataException;
import ua.cinema.repository.Identity;
import ua.cinema.util.ValidationUtils;

public class Actor implements Comparable<Actor>, Identity {

    private static final Logger logger = LoggerFactory.getLogger(Actor.class);

    @NotNull(message = "firstName cannot be null")
    @NotBlank(message = "firstName cannot be blank")
    @Size(min = 2, max = 20, message = "firstName must be between 2 and 20 characters")
    @Pattern(regexp = "^[\\p{L}\\s\\-']+$", message = "firstName must contain only letters, spaces, hyphens, or apostrophes")
    private String firstName;

    @NotNull(message = "lastName cannot be null")
    @NotBlank(message = "lastName cannot be blank")
    @Size(min = 2, max = 20, message = "lastName must be between 2 and 20 characters")
    @Pattern(regexp = "^[\\p{L}\\s\\-']+$", message = "lastName must contain only letters, spaces, hyphens, or apostrophes")
    private String lastName;

    @Min(value = 1850, message = "birthYear must be >= 1850")
    @Max(value = 2025, message = "birthYear cannot be in the future")
    private int birthYear;

    private static final Comparator<Actor> ACTOR_COMPARATOR =
            Comparator.comparing(Actor::getLastName)
                    .thenComparing(Actor::getFirstName)
                    .thenComparingInt(Actor::getBirthYear);

    public Actor(String firstName, String lastName, int birthYear) {
        logger.debug("Attempting to create Actor: firstName={}, lastName={}, birthYear={}",
                firstName, lastName, birthYear);

        this.firstName = formatName(firstName);
        this.lastName = formatName(lastName);
        this.birthYear = birthYear;

        try {
            ValidationUtils.validate(this);
            logger.info("Actor successfully created: {}", this);
        } catch (InvalidDataException e) {
            logger.error("Failed to create Actor: {}", e.getMessage());
            throw e;
        }
    }

    public static Actor createActor(String firstName, String lastName, int birthYear) {
        return new Actor(firstName, lastName, birthYear);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        String old = this.firstName;
        this.firstName = formatName(firstName);
        logger.debug("Attempting to set firstName='{}'", this.firstName);

        try {
            ValidationUtils.validate(this);
            logger.info("firstName successfully updated to '{}'", this.firstName);
        } catch (InvalidDataException e) {
            this.firstName = old;
            logger.error("Failed to update firstName: {}", e.getMessage());
            throw e;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        String old = this.lastName;
        this.lastName = formatName(lastName);
        logger.debug("Attempting to set lastName='{}'", this.lastName);

        try {
            ValidationUtils.validate(this);
            logger.info("lastName successfully updated to '{}'", this.lastName);
        } catch (InvalidDataException e) {
            this.lastName = old;
            logger.error("Failed to update lastName: {}", e.getMessage());
            throw e;
        }
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        int old = this.birthYear;
        this.birthYear = birthYear;
        logger.debug("Attempting to set birthYear='{}'", this.birthYear);

        try {
            ValidationUtils.validate(this);
            logger.info("birthYear successfully updated to '{}'", this.birthYear);
        } catch (InvalidDataException e) {
            this.birthYear = old;
            logger.error("Failed to update birthYear: {}", e.getMessage());
            throw e;
        }
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
        if (name == null) return null;
        name = name.trim();
        if (name.isEmpty()) return name;
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Actor actor)) return false;
        return birthYear == actor.birthYear &&
                firstName.equals(actor.firstName) &&
                lastName.equals(actor.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, birthYear);
    }

    @Override
    public int compareTo(@NotNull Actor other) {
        return ACTOR_COMPARATOR.compare(this, other);
    }

    @Override
    public String getIdentity() {
        return firstName + " " + lastName + " (" + birthYear + ")";
    }
}
