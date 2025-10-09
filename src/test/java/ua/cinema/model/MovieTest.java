package ua.cinema.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ua.cinema.exception.InvalidDataException;

import java.time.LocalDate;
import java.util.stream.Stream;

class MovieTest {

    @Test
    void testMovieCreationValidData() {
        Movie movie = new Movie("Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16));

        assertThat(movie.title()).isEqualTo("Inception");
        assertThat(movie.genre()).isEqualTo(Genre.ACTION);
        assertThat(movie.durationMinutes()).isEqualTo(148);
        assertThat(movie.releaseDate()).isEqualTo(LocalDate.of(2010, 7, 16));
    }

    @Test
    void testMovieCreationInvalidTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Movie("", Genre.DRAMA, 120, LocalDate.of(2020, 1, 1));
        });
    }

    @Test
    void testMovieCreationInvalidGenre() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Movie("Some Movie", null, 120, LocalDate.of(2020, 1, 1));
        });
    }

    @Test
    void testMovieCreationInvalidDuration() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Movie("Some Movie", Genre.COMEDY, -10, LocalDate.of(2020, 1, 1));
        });
    }

    @Test
    void testMovieCreationInvalidReleaseDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Movie("Some Movie", Genre.HORROR, 100, null);
        });
    }

    @Test
    void testGetGenreDescription() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(Movie.getGenreDescription(Genre.ACTION)).isEqualTo("Movies with fights and explosions");
        softly.assertThat(Movie.getGenreDescription(Genre.DRAMA)).isEqualTo("Movies about life and emotions");
        softly.assertThat(Movie.getGenreDescription(Genre.COMEDY)).isEqualTo("Movies for a good mood");
        softly.assertThat(Movie.getGenreDescription(Genre.HORROR)).isEqualTo("Movies that scare");
        softly.assertThat(Movie.getGenreDescription(Genre.DOCUMENTARY)).isEqualTo("Movies about real events");
        softly.assertAll();
    }

    @Test
    void testFactoryMethodOf() {
        Movie movie = Movie.of("Interstellar", Genre.DRAMA, 169, LocalDate.of(2014, 11, 7));
        assertThat(movie).isNotNull();
        assertThat(movie.title()).isEqualTo("Interstellar");
        assertThat(movie.genre()).isEqualTo(Genre.DRAMA);
    }

    @DisplayName("Parameterized test for valid movie creation")
    @ParameterizedTest
    @MethodSource("provideValidMovies")
    void testMovieCreationParameterized(String title, Genre genre, int duration, LocalDate releaseDate) {
        Movie movie = new Movie(title, genre, duration, releaseDate);

        assertThat(movie.title()).isEqualTo(title);
        assertThat(movie.genre()).isEqualTo(genre);
        assertThat(movie.durationMinutes()).isEqualTo(duration);
        assertThat(movie.releaseDate()).isEqualTo(releaseDate);
    }

    static Stream<Arguments> provideValidMovies() {
        return Stream.of(
                Arguments.of("Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16)),
                Arguments.of("Interstellar", Genre.DRAMA, 169, LocalDate.of(2014, 11, 7)),
                Arguments.of("The Hangover", Genre.COMEDY, 100, LocalDate.of(2009, 6, 5))
        );
    }

}
