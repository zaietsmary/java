package ua.cinema.model;

import java.time.LocalDate;

import ua.cinema.util.MovieUtils;

public record Movie(String title, Genre genre, int durationMinutes, LocalDate releaseDate) {

    public Movie {
        if(!MovieUtils.isValidTitle(title)){
            throw new IllegalArgumentException("Invalid title: " + title);
        }
        if(!MovieUtils.isValidGenre(genre)){
            throw new IllegalArgumentException("Invalid genre: " + genre);
        }
        if(!MovieUtils.isValidDuration(durationMinutes)){
            throw new IllegalArgumentException("Invalid duration: " + durationMinutes);
        }
        if(!MovieUtils.isValidReleaseDate(releaseDate)){
            throw new IllegalArgumentException("Invalid releaseDate: " + releaseDate);
        }
    }


    public static String getGenreDescription(Genre genre) {
        return switch (genre) {
            case ACTION      -> "Movies with fights and explosions";
            case DRAMA       -> "Movies about life and emotions";
            case COMEDY      -> "Movies for a good mood";
            case HORROR      -> "Movies that scare";
            case DOCUMENTARY -> "Movies about real events";
        };
    }

    public static Movie of(String title, Genre genre, int durationMinutes, LocalDate releaseDate) {
        return new Movie(title, genre, durationMinutes, releaseDate);
    }
}
