package ua.cinema.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.model.Ticket;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TicketRepository extends GenericRepository<Ticket> {
    private static final Logger logger = LoggerFactory.getLogger(TicketRepository.class);

    public TicketRepository() {
        super(ticket -> ticket.screening().getMovie().title() + "_" +
                        ticket.screening().getHall().getHallNumber() + "_" +
                        ticket.screening().getScreeningDateTime() + "_" +
                        ticket.seatNumber(),
                "Ticket");
    }


    /**
     * Find tickets by price.
     *
     * @param price price to search for
     * @return list of tickets with the specified price
     */
    public List<Ticket> findByPrice(double price) {
        List<Ticket> results = getAll().stream()
                .filter(ticket -> ticket.price() == price)
                .collect(Collectors.toList());

        logger.info("Found {} tickets with price {}", results.size(), price);
        return results;
    }

    /**
     * Find tickets by status (case-insensitive, partial match).
     * Uses Stream API filter operation.
     *
     * @param ticketStatus partial status name to search for
     * @return list of tickets matching the status
     */
    public List<Ticket> findByStatus(String ticketStatus) {
        if (ticketStatus == null || ticketStatus.trim().isEmpty()) {
            logger.warn("Attempted to search with null or empty status");
            return List.of();
        }

        String searchTerm = ticketStatus.trim().toLowerCase();
        List<Ticket> results = getAll().stream()
                .filter(ticket -> ticket.ticketStatus().name().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        logger.info("Found {} tickets with status containing '{}'", results.size(), ticketStatus);
        return results;
    }

    /**
     * Sort tickets by screening, seat number, price, and status (all ascending).
     * This method does not modify the repository - it returns a new sorted copy.
     *
     * @return new sorted list of tickets
     */
    public List<Ticket> sortBySeatNumber() {
        List<Ticket> allTickets = getAll();
        allTickets.sort(
                Comparator.comparing(Ticket::screening)
                        .thenComparing(Ticket::seatNumber)
                        .thenComparing(Ticket::price)
                        .thenComparing(Ticket::ticketStatus)
        );
        logger.info("Sorted {} by screening, seat number, price, and status", "Ticket");
        return allTickets;
    }

    /**
     * Sort tickets by movie title in ascending order.
     * This method does not modify the repository - it returns a new sorted copy.
     *
     * @return new sorted list of tickets
     */
    public List<Ticket> sortByMovie() {
        List<Ticket> allTickets = getAll()
                .stream()
                .sorted(Comparator.comparing(ticket -> ticket.screening().getMovie().title()))
                .toList();

        logger.info("Sorted {} by movie title in ascending order", "Ticket");
        return allTickets;
    }

    /**
     * Group tickets by movie genre.
     *
     * @return Map of genre (as String) to list of tickets
     */
    public java.util.Map<String, List<Ticket>> groupByGenre() {
        java.util.Map<String, List<Ticket>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(ticket -> ticket.screening().getMovie().genre().toString()));

        logger.info("Grouped tickets by genre: {} genres", grouped.size());
        return grouped;
    }

    /**
     * Group tickets by price.
     * Uses Stream API collect with groupingBy.
     *
     * @return Map of price to list of tickets
     */
    public java.util.Map<Double, List<Ticket>> groupByPrice() {
        java.util.Map<Double, List<Ticket>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(Ticket::price));

        logger.info("Grouped tickets by price: {} price points", grouped.size());
        return grouped;
    }

    /**
     * Count tickets by status.
     *
     * @return Map of ticket status (as String) to count
     */
    public java.util.Map<String, Long> countByStatus() {
        java.util.Map<String, Long> counts = getAll().stream()
                .collect(Collectors.groupingBy(
                        ticket -> ticket.ticketStatus().name().toLowerCase(),
                        Collectors.counting()
                ));

        logger.info("Ticket counts by status: {}", counts);
        return counts;
    }

}
