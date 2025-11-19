package ua.cinema;

import ua.cinema.model.*;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        Movie[] movies = new Movie[3];
        try {
            movies[0] = Movie.createMovie("Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16));
            movies[1] = Movie.createMovie("Interstellar", Genre.DRAMA, 169, LocalDate.of(2014, 11, 7));
            movies[2] = Movie.createMovie("The Hangover", Genre.COMEDY, 100, LocalDate.of(2009, 6, 5));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Помилка створення фільму: " + e.getMessage());
        }

        Hall[] halls = new Hall[2];
        try {
            halls[0] = Hall.createHall(1, 100);
            halls[1] = Hall.createHall(2, 120);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Помилка створення залу: " + e.getMessage());
        }

        Screening[] screenings = new Screening[3];
        try {
            screenings[0] = Screening.createScreening(movies[0], halls[0], LocalDate.of(2025, 10, 10));
            screenings[1] = Screening.createScreening(movies[1], halls[1], LocalDate.of(2025, 10, 11));
            screenings[2] = Screening.createScreening(movies[2], halls[0], LocalDate.of(2025, 10, 12));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Помилка створення сеансу: " + e.getMessage());
        }

        Ticket[] tickets = new Ticket[5];
        try {
            tickets[0] = Ticket.createTicket(screenings[0], 1, 150.0, TicketStatus.AVAILABLE);
            tickets[1] = Ticket.createTicket(screenings[0], 2, 150.0, TicketStatus.RESERVED);
            tickets[2] = Ticket.createTicket(screenings[1], 5, 200.0, TicketStatus.SOLD);
            tickets[3] = Ticket.createTicket(screenings[2], 10, 120.0, TicketStatus.AVAILABLE);
            tickets[4] = Ticket.createTicket(screenings[2], 11, 120.0, TicketStatus.CANCELED);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Помилка створення квитка: " + e.getMessage());
        }

        System.out.println("=== Список квитків ===");
        for (Ticket ticket : tickets) {
            if (ticket != null) {
                System.out.println("Фільм: " + ticket.screening().getMovie().title());
                System.out.println("Зал: " + ticket.screening().getHall().getHallInfo());
                System.out.println("Дата сеансу: " + ticket.screening().getScreeningDateTime());
                System.out.println("Місце: " + ticket.seatNumber());
                System.out.println("Ціна: " + ticket.price());
                System.out.println("Статус: " + Ticket.getTicketStatus(ticket.ticketStatus()));
                System.out.println("-----------------------------");
            }
        }

        try {
            Ticket invalidTicket = Ticket.createTicket(screenings[0], -1, -50.0, null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Спроба створити некоректний квиток: " + e.getMessage());
        }

        logger.info("Демонстрація Movie, Hall, Screening та Ticket завершена.");
    }

}
