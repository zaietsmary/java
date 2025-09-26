package ua.cinema.model;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Actor actor = Actor.of("Tom", "Hanks", 1956);
        System.out.println(actor.getFullName());

        Hall hall = Hall.of(1, 100);
        System.out.println(hall.getHallInfo());

        Movie movie = Movie.of("Forrest Gump", Genre.DRAMA, 142, LocalDate.of(1994, 7, 6));
        System.out.println(movie.title() + " - " + Movie.getGenreDescription(movie.genre()));

        String rating = switch (movie.genre()) {
            case ACTION, HORROR -> "High intensity";
            case DRAMA -> "Emotional";
            case COMEDY -> "Funny";
            case DOCUMENTARY -> "Informative";
        };
        System.out.println("Rating: " + rating);

        Movie movie2 = Movie.of("Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16));
        Hall hall2 = Hall.of(2, 120);
        Screening screening = Screening.of(movie, hall, LocalDate.of(2025, 10, 1));

        Ticket ticket = Ticket.of(screening, 5, 250.0, TicketStatus.AVAILABLE);

        System.out.println("Movie: " + movie2.title() + " - " + Movie.getGenreDescription(movie2.genre()));
        System.out.println("Screening in " + hall2.getHallInfo() + " at " + screening.screeningDateTime());
        System.out.println("Ticket seat " + ticket.seatNumber() + ": " + Ticket.getTicketStatus(ticket.ticketStatus()));

    }
}
