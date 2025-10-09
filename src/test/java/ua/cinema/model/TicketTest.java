package ua.cinema.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketTest {

    static Movie sampleMovie() {
        return new Movie("Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16));
    }

    static Hall sampleHall() {
        return new Hall(1, 100);
    }

    static Screening sampleScreening() {
        return new Screening(sampleMovie(), sampleHall(), LocalDate.of(2025, 10, 10));
    }

    @DisplayName("Parameterized test for valid ticket creation")
    @ParameterizedTest
    @MethodSource("provideValidTickets")
    void testTicketCreationValid(Screening screening, int seatNumber, double price, TicketStatus status) {
        Ticket ticket = new Ticket(screening, seatNumber, price, status);

        assertThat(ticket.screening()).isEqualTo(screening);
        assertThat(ticket.seatNumber()).isEqualTo(seatNumber);
        assertThat(ticket.price()).isEqualTo(price);
        assertThat(ticket.ticketStatus()).isEqualTo(status);
    }

    static Stream<Arguments> provideValidTickets() {
        Screening screening = sampleScreening();
        return Stream.of(
                Arguments.of(screening, 1, 100.0, TicketStatus.AVAILABLE),
                Arguments.of(screening, 5, 150.0, TicketStatus.RESERVED),
                Arguments.of(screening, 10, 200.0, TicketStatus.SOLD)
        );
    }

    @DisplayName("Parameterized test for invalid seat numbers")
    @ParameterizedTest
    @MethodSource("provideInvalidSeatNumbers")
    void testTicketCreationInvalidSeatNumber(Screening screening, int seatNumber, double price, TicketStatus status) {
        assertThrows(IllegalArgumentException.class, () -> new Ticket(screening, seatNumber, price, status));
    }

    static Stream<Arguments> provideInvalidSeatNumbers() {
        Screening screening = sampleScreening();
        return Stream.of(
                Arguments.of(screening, -1, 100.0, TicketStatus.AVAILABLE),
                Arguments.of(screening, 0, 120.0, TicketStatus.RESERVED)
        );
    }

    @DisplayName("Parameterized test for invalid prices")
    @ParameterizedTest
    @MethodSource("provideInvalidPrices")
    void testTicketCreationInvalidPrice(Screening screening, int seatNumber, double price, TicketStatus status) {
        assertThrows(IllegalArgumentException.class, () -> new Ticket(screening, seatNumber, price, status));
    }

    static Stream<Arguments> provideInvalidPrices() {
        Screening screening = sampleScreening();
        return Stream.of(
                Arguments.of(screening, 1, -10.0, TicketStatus.AVAILABLE),
                Arguments.of(screening, 2, 0.0, TicketStatus.RESERVED)
        );
    }

    @DisplayName("Parameterized test for invalid ticket status")
    @ParameterizedTest
    @MethodSource("provideInvalidStatuses")
    void testTicketCreationInvalidStatus(Screening screening, int seatNumber, double price, TicketStatus status) {
        assertThrows(IllegalArgumentException.class, () -> new Ticket(screening, seatNumber, price, status));
    }

    static Stream<Arguments> provideInvalidStatuses() {
        Screening screening = sampleScreening();
        return Stream.of(
                Arguments.of(screening, 1, 100.0, null)
        );
    }

    @Test
    void testGetTicketStatus() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(Ticket.getTicketStatus(TicketStatus.AVAILABLE)).isEqualTo("Available");
        softly.assertThat(Ticket.getTicketStatus(TicketStatus.RESERVED)).isEqualTo("Reserved");
        softly.assertThat(Ticket.getTicketStatus(TicketStatus.SOLD)).isEqualTo("Sold");
        softly.assertThat(Ticket.getTicketStatus(TicketStatus.CANCELED)).isEqualTo("Canceled");
        softly.assertAll();
    }

    @Test
    void testFactoryMethodOf() {
        Screening screening = sampleScreening();
        Ticket ticket = Ticket.of(screening, 3, 120.0, TicketStatus.AVAILABLE);

        assertThat(ticket).isNotNull();
        assertThat(ticket.screening()).isEqualTo(screening);
        assertThat(ticket.seatNumber()).isEqualTo(3);
        assertThat(ticket.price()).isEqualTo(120.0);
        assertThat(ticket.ticketStatus()).isEqualTo(TicketStatus.AVAILABLE);
    }
}
