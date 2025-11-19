package ua.cinema.validation;

import org.junit.jupiter.api.Test;
import ua.cinema.exception.InvalidDataException;
import ua.cinema.model.Genre;
import ua.cinema.model.Movie;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovieTest {

    // 1. Позитивний тест: створення з валідними даними
    @Test
    void shouldCreateMovieWithValidData() {
        Movie movie = new Movie("Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16));

        assertThat(movie.title()).isEqualTo("Inception");
        assertThat(movie.genre()).isEqualTo(Genre.ACTION);
        assertThat(movie.durationMinutes()).isEqualTo(148);
        assertThat(movie.releaseDate()).isEqualTo(LocalDate.of(2010, 7, 16));
    }

    // 2. Негативний тест: некоректні дані
    @Test
    void shouldThrowExceptionForInvalidMovieCreation() {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () ->
                new Movie(null, null, 0, null)
        );

        assertThat(exception.getMessage())
                .contains("title: invalid value 'null' — title cannot be blank")
                .contains("genre: invalid value 'null' — genre cannot be null")
                .contains("durationMinutes: invalid value '0' — duration must be >= 1")
                .contains("releaseDate: invalid value 'null' — releaseDate cannot be null");
    }

    // 3. Позитивний тест через фабрику
    @Test
    void shouldCreateMovieViaFactory() {
        Movie movie = Movie.createMovie("Interstellar", Genre.DRAMA, 169, LocalDate.of(2014, 11, 7));

        assertThat(movie.title()).isEqualTo("Interstellar");
        assertThat(movie.genre()).isEqualTo(Genre.DRAMA);
        assertThat(movie.durationMinutes()).isEqualTo(169);
        assertThat(movie.releaseDate()).isEqualTo(LocalDate.of(2014, 11, 7));
    }

    // 4. Негативний тест через фабрику
    @Test
    void shouldThrowExceptionForInvalidMovieFactory() {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () ->
                Movie.createMovie(null, null, 0, null)
        );

        assertThat(exception.getMessage())
                .contains("title: invalid value 'null' — title cannot be blank")
                .contains("genre: invalid value 'null' — genre cannot be null")
                .contains("durationMinutes: invalid value '0' — duration must be >= 1")
                .contains("releaseDate: invalid value 'null' — releaseDate cannot be null");
    }

    // 5. Позитивний тест: перевірка, що код не викидає виключень
    @Test
    void shouldAllowValidMovieCreation() {
        assertThatCode(() -> new Movie("The Hangover", Genre.COMEDY, 100, LocalDate.of(2009, 6, 5)))
                .doesNotThrowAnyException();
    }
}
