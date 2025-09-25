package ua.cinema.model;

import java.time.LocalDate;
import java.util.Objects;

import ua.cinema.util.MovieUtils;

public class Movie {
    private String title;
    String genre;
    protected int durationMinutes;
    private LocalDate releaseDate;

    public Movie() {
    }

    public Movie(String title, String genre, int durationMinutes, LocalDate releaseDate) {
        if(!MovieUtils.isValidTitle(title)){
            throw new IllegalArgumentException("Invalid title: " + title);
        }
        if(!MovieUtils.isValidGenre(genre)){
            throw new IllegalArgumentException("Invalid genre: " + genre);
        }
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        if(!MovieUtils.isValidTitle(title)){
            throw new IllegalStateException("Invalid title: " + title);
        }
        return title;
    }

    public void setTitle(String title) {
        if(!MovieUtils.isValidTitle(title)){
            throw new IllegalArgumentException("Invalid title: " + title);
        }
        this.title = title;
    }

    public String getGenre() {
        if(!MovieUtils.isValidGenre(genre)){
            throw new IllegalStateException("Invalid genre: " + genre);
        }
        return genre;
    }

    public void setGenre(String genre) {
        if(!MovieUtils.isValidGenre(genre)){
            throw new IllegalArgumentException("Invalid genre: " + genre);
        }
        this.genre = genre;
    }

    public int getDurationMinutes() {
        if(!MovieUtils.isValidDuration(durationMinutes)){
            throw new IllegalStateException("Invalid duration: " + durationMinutes);
        }
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        if(!MovieUtils.isValidDuration(durationMinutes)){
            throw new IllegalArgumentException("Invalid duration: " + durationMinutes);
        }
        this.durationMinutes = durationMinutes;
    }

    public LocalDate getReleaseDate() {
        if(!MovieUtils.isValidReleaseDate(releaseDate)){
            throw new IllegalStateException("Invalid release date: " + releaseDate);
        }
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        if(!MovieUtils.isValidReleaseDate(releaseDate)){
            throw new IllegalArgumentException("Invalid release date: " + releaseDate);
        }
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", durationMinutes=" + durationMinutes +
                ", releaseDate=" + releaseDate +
                '}';
    }

    public String getMovieInfo() {
        return title + " (" + releaseDate + ") - " + genre + ", " + durationMinutes + " min";
    }

    public static String formatTitle(String title) {
        if (title == null) {
            return null;
        }
        title = title.trim();
        if (title.isEmpty()) {
            return title;
        }
        return Character.toUpperCase(title.charAt(0)) + title.substring(1).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return durationMinutes == movie.durationMinutes &&
                releaseDate == movie.releaseDate &&
                title.equals(movie.title) &&
                genre.equals(movie.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, genre, durationMinutes, releaseDate);
    }

    public static Movie of(String title, String genre, int durationMinutes, LocalDate releaseDate) {
        return new Movie(title, genre, durationMinutes, releaseDate);
    }
}
