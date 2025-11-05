package ua.cinema.repository;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.model.Ticket;
import ua.cinema.model.Screening;
import ua.cinema.model.Movie;
import ua.cinema.model.Hall;
import ua.cinema.model.Genre;
import java.time.LocalDate;
import java.util.List;
import ua.cinema.model.TicketStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TicketRepository Tests")
public class TicketRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(TicketRepositoryTest.class);

    private TicketRepository ticketRepository;
    private Ticket ticket1;
    private Ticket ticket2;
    private Ticket ticket3;
    private Ticket ticket4;
    private Ticket ticket5;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data");
        ticketRepository = new TicketRepository();

        Movie movie1 = new Movie("Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16));
        Movie movie2 = new Movie("Interstellar",  Genre.ACTION, 169, LocalDate.of(2014, 11, 7));

        Hall hall1 = new Hall(1, 120);
        Hall hall2 = new Hall(2, 200);

        Screening screening1 = new Screening(movie1, hall1, LocalDate.of(2025, 11, 10));
        Screening screening2 = new Screening(movie2, hall2, LocalDate.of(2025, 11, 11));

        ticket1 = new Ticket(screening1, 1, 150.0, TicketStatus.AVAILABLE);
        ticket2 = new Ticket(screening1, 2, 150.0, TicketStatus.RESERVED);
        ticket3 = new Ticket(screening1, 3, 200.0, TicketStatus.SOLD);
        ticket4 = new Ticket(screening2, 1, 180.0, TicketStatus.AVAILABLE);
        ticket5 = new Ticket(screening2, 2, 180.0, TicketStatus.CANCELED);

        ticketRepository.add(ticket1);
        ticketRepository.add(ticket2);
        ticketRepository.add(ticket3);
        ticketRepository.add(ticket4);
        ticketRepository.add(ticket5);

        logger.info("Test setup completed with {} tickets", ticketRepository.size());
    }

    @Test
    @DisplayName("findByPrice should find all tickets with the specified price")
    void testFindByPrice() {
        logger.info("Testing findByPrice");

        List<Ticket> results = ticketRepository.findByPrice(150.0);

        assertThat(results)
                .as("Should find 2 tickets with price 150.0")
                .hasSize(2);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(results)
                .as("All found tickets should have price 150.0")
                .allMatch(ticket -> ticket.price() == 150.0);
        softly.assertAll();
        logger.info("findByPrice test completed successfully");
    }

    @Test
    @DisplayName("findByStatus should find tickets by status (case-insensitive)")
    void testFindByStatus() {
        logger.info("Testing findByStatus with case-insensitive match");

        List<Ticket> results = ticketRepository.findByStatus("SOLD");

        assertThat(results)
                .as("Should find tickets with status 'SOLD'")
                .hasSize(1);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(results)
                .as("All results should have status SOLD (case-insensitive)")
                .allMatch(ticket -> ticket.ticketStatus().name().equalsIgnoreCase("SOLD"));

        softly.assertAll();
        logger.info("findByStatus test completed successfully");
    }

    @Test
    @DisplayName("sortByMovie should sort tickets by movie title and seat number")
    void testSortByMovie() {
        logger.info("Testing sortByMovie");

        List<Ticket> sorted = ticketRepository.sortByMovie();

        assertThat(sorted)
                .as("Should return all 5 tickets")
                .hasSize(5);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(sorted.get(0).screening().getMovie().title())
                .as("First should be Inception")
                .isEqualTo("Inception");

        softly.assertThat(sorted.get(3).screening().getMovie().title())
                .as("Fourth should be Interstellar")
                .isEqualTo("Interstellar");

        softly.assertThat(sorted.get(0).seatNumber())
                .as("First Inception ticket should be seat 1")
                .isEqualTo(1);

        softly.assertThat(sorted.get(1).seatNumber())
                .as("Second Inception ticket should be seat 2")
                .isEqualTo(2);

        softly.assertThat(sorted.get(2).seatNumber())
                .as("Third Inception ticket should be seat 3")
                .isEqualTo(3);

        softly.assertAll();
        logger.info("sortByMovie test completed successfully");
    }

    @Test
    @DisplayName("sortByMovie should not modify the original repository")
    void testSortByMovieDoesNotModifyRepository() {
        logger.info("Testing that sortByMovie does not modify repository");

        List<Ticket> originalOrder = List.copyOf(ticketRepository.getAll());

        List<Ticket> sorted = ticketRepository.sortByMovie();

        List<Ticket> currentOrder = ticketRepository.getAll();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(currentOrder)
                .as("Repository order should remain unchanged after sorting")
                .isEqualTo(originalOrder);

        softly.assertThat(sorted)
                .as("Sorted list should be a new instance, not the same as repository's list")
                .isNotSameAs(currentOrder);

        softly.assertAll();
        logger.info("sortByMovie does not modify repository - test completed successfully");
    }

}
