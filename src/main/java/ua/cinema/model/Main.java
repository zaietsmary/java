package ua.cinema.model;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Actor actor = new Actor("Anastasiia", "Golovata", 2002);
        System.out.println(actor);

        Movie m1 = new Movie("Inception", "Sci-Fi", 148, LocalDate.of(2010, 7, 16));
        System.out.println(m1);
        System.out.println("Movie genre (package-private): " + m1.genre);
        System.out.println("Movie duration (protected): " + m1.durationMinutes + " minutes");

        Movie m2 = Movie.of("Interstellar", "Sci-Fi", 169, LocalDate.of(2014, 11, 7));

        Movie m3 = new Movie();
        m3.setTitle("The Dark Knight");
        m3.setGenre("Action");
        m3.setDurationMinutes(152);
        m3.setReleaseDate(LocalDate.of(2008, 7, 18));
        System.out.println(m2);

        try {
            Movie badMovie = new Movie("Invalid", "Error", -100, LocalDate.now());
            System.out.println("Should not print: " + badMovie);
        } catch (IllegalArgumentException e) {
            System.out.println("Validation failed (negative duration): " + e.getMessage());
        }

        try {
            Actor badActor = new Actor("", "", 3000);
            System.out.println("Should not print: " + badActor);
        } catch (IllegalArgumentException e) {
            System.out.println("Validation failed (invalid actor data): " + e.getMessage());
        }

        Hall hall = new Hall(2, 45);
        Screening screening = Screening.of(m1, hall, LocalDate.of(2025, 9, 21));
        System.out.println(screening);
    }

}
