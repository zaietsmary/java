package ua.cinema.validation;

import org.junit.jupiter.api.Test;
import ua.cinema.exception.InvalidDataException;
import ua.cinema.model.Actor;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActorTest {

    //Створення з валідними даними
    @Test
    void shouldCreateActorWithValidData() {
        Actor actor = new Actor("John", "Doe", 1980);

        assertThat(actor.getFirstName()).isEqualTo("John");
        assertThat(actor.getLastName()).isEqualTo("Doe");
        assertThat(actor.getBirthYear()).isEqualTo(1980);
    }

    // Спроби створення з некоректними даними
    @Test
    void shouldThrowExceptionForInvalidActorOnCreation() {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            new Actor("", "D", 3000);
        });

        assertThat(exception.getMessage())
                .contains("firstName: invalid value '' — firstName cannot be blank")
                .contains("lastName: invalid value 'D' — lastName must be between 2 and 20 characters")
                .contains("birthYear: invalid value '3000' — birthYear cannot be in the future");
    }

    // Коректність валідації в сеттерах
    @Test
    void shouldNotAllowInvalidFirstNameSetter() {
        Actor actor = new Actor("John", "Doe", 1980);

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            actor.setFirstName("");
        });

        assertThat(exception.getMessage())
                .contains("firstName: invalid value '' — firstName cannot be blank");

        assertThat(actor.getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldNotAllowInvalidLastNameSetter() {
        Actor actor = new Actor("John", "Doe", 1980);

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            actor.setLastName("1");
        });

        assertThat(exception.getMessage())
                .contains("lastName: invalid value '1' — lastName must be between 2 and 20 characters")
                .contains("lastName: invalid value '1' — lastName must contain only letters, spaces, hyphens, or apostrophes");

        assertThat(actor.getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldNotAllowInvalidBirthYearSetter() {
        Actor actor = new Actor("John", "Doe", 1980);

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            actor.setBirthYear(3000);
        });

        assertThat(exception.getMessage())
                .contains("birthYear: invalid value '3000' — birthYear cannot be in the future");

        assertThat(actor.getBirthYear()).isEqualTo(1980);
    }

    @Test
    void shouldAllowValidValuesInSetters() {
        Actor actor = new Actor("John", "Doe", 1980);

        assertThatCode(() -> {
            actor.setFirstName("Michael");
            actor.setLastName("Smith");
            actor.setBirthYear(1995);
        }).doesNotThrowAnyException();

        assertThat(actor.getFirstName()).isEqualTo("Michael");
        assertThat(actor.getLastName()).isEqualTo("Smith");
        assertThat(actor.getBirthYear()).isEqualTo(1995);
    }
}
