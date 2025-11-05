package ua.cinema;

import ua.cinema.model.*;
import ua.cinema.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MainStream {
    public static void main(String[] args) {
        System.out.println("🎬 Cinema Management System Demo\n");

        TicketRepository ticketRepository = new TicketRepository();

        Movie inception = new Movie("Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16));
        Movie interstellar = new Movie("Interstellar", Genre.ACTION, 169, LocalDate.of(2014, 11, 7));
        Movie soul = new Movie("Soul", Genre.COMEDY, 100, LocalDate.of(2020, 12, 25));

        Hall hall1 = new Hall(1, 120);
        Hall hall2 = new Hall(2, 200);

        Screening screening1 = new Screening(inception, hall1, LocalDate.of(2025, 11, 10));
        Screening screening2 = new Screening(interstellar, hall2, LocalDate.of(2025, 11, 11));
        Screening screening3 = new Screening(soul, hall1, LocalDate.of(2025, 11, 12));

        Ticket t1 = new Ticket(screening1, 1, 150.0, TicketStatus.AVAILABLE);
        Ticket t2 = new Ticket(screening1, 2, 150.0, TicketStatus.SOLD);
        Ticket t3 = new Ticket(screening2, 1, 180.0, TicketStatus.RESERVED);
        Ticket t4 = new Ticket(screening2, 2, 180.0, TicketStatus.AVAILABLE);
        Ticket t5 = new Ticket(screening3, 1, 200.0, TicketStatus.CANCELED);

        ticketRepository.add(t1);
        ticketRepository.add(t2);
        ticketRepository.add(t3);
        ticketRepository.add(t4);
        ticketRepository.add(t5);

        System.out.println("! Added 5 tickets to repository.\n");

        System.out.println("! Find tickets by price 150:");
        List<Ticket> ticketsByPrice = ticketRepository.findByPrice(150.0);
        ticketsByPrice.forEach(System.out::println);

        System.out.println("\n! Find tickets with status AVAILABLE:");
        List<Ticket> availableTickets = ticketRepository.findByStatus("AVAILABLE");
        availableTickets.forEach(System.out::println);

        System.out.println("\n! Sort tickets by movie title:");
        List<Ticket> sortedByMovie = ticketRepository.sortByMovie();
        sortedByMovie.forEach(System.out::println);

        System.out.println("\n! Stream API demo — average ticket price:");
        double avgPrice = ticketRepository.getAll().stream()
                .mapToDouble(Ticket::price)
                .average()
                .orElse(0);
        System.out.println("Average price = " + avgPrice);

        System.out.println("\n! Tickets grouped by status:");
        ticketRepository.getAll().stream()
                .collect(Collectors.groupingBy(Ticket::ticketStatus))
                .forEach((status, tickets) ->
                        System.out.println(status + " -> " + tickets.size() + " tickets"));

        System.out.println("\n! Comparing performance: stream vs parallelStream");

        List<Ticket> allTickets = ticketRepository.getAll();

        long startStream = System.nanoTime();
        long countAvailable = allTickets.stream()
                .filter(t -> t.ticketStatus() == TicketStatus.AVAILABLE)
                .count();
        long endStream = System.nanoTime();

        long startParallel = System.nanoTime();
        long countAvailableParallel = allTickets.parallelStream()
                .filter(t -> t.ticketStatus() == TicketStatus.AVAILABLE)
                .count();
        long endParallel = System.nanoTime();

        System.out.printf("Normal stream count: %d (time: %d ns)%n", countAvailable, (endStream - startStream));
        System.out.printf("Parallel stream count: %d (time: %d ns)%n", countAvailableParallel, (endParallel - startParallel));

        System.out.println("\n! Demo completed successfully!");
    }
}
