package ua.cinema.model;

import jakarta.validation.constraints.*;
        import ua.cinema.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Comparator;
import ua.cinema.exception.InvalidDataException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record Ticket(
        @NotNull(message = "screening cannot be null")
        Screening screening,

        @Min(value = 1, message = "seatNumber must be >= 1")
        int seatNumber,

        @Positive(message = "price must be > 0")
        double price,

        @NotNull(message = "ticketStatus cannot be null")
        TicketStatus ticketStatus
) implements Comparable<Ticket> {

    private static final Logger logger = LoggerFactory.getLogger(Ticket.class);

    public static final Comparator<Ticket> TICKET_COMPARATOR =
            Comparator.comparing(Ticket::screening)
                    .thenComparing(Ticket::seatNumber)
                    .thenComparing(Ticket::price)
                    .thenComparing(Ticket::ticketStatus);

    public Ticket(Screening screening, int seatNumber, double price, TicketStatus ticketStatus) {
        this.screening = screening;
        this.seatNumber = seatNumber;
        this.price = price;
        this.ticketStatus = ticketStatus;

        logger.debug("Attempting to validate Ticket: screening={}, seat={}, price={}, status={}",
                screening, seatNumber, price, ticketStatus);

        try {
            ValidationUtils.validate(this);
            logger.info("Ticket successfully created: {}", this);
        } catch (InvalidDataException e) {
            logger.error("Validation failed for Ticket: {}", e.getMessage());
            throw e;
        }
    }

    public static Ticket createTicket(Screening screening, int seatNumber, double price, TicketStatus status) {
        logger.debug("Factory: attempting to create Ticket: screening={}, seat={}, price={}, status={}",
                screening, seatNumber, price, status);
        try {
            Ticket ticket = new Ticket(screening, seatNumber, price, status);
            logger.info("Factory: created Ticket {}", ticket);
            return ticket;
        } catch (InvalidDataException e) {
            logger.error("Factory: failed to create Ticket: {}", e.getMessage());
            throw e;
        }
    }

    public static String getTicketStatus(TicketStatus ticketStatus) {
        return switch (ticketStatus) {
            case RESERVED -> "Reserved";
            case AVAILABLE -> "Available";
            case SOLD -> "Sold";
            case CANCELED -> "Canceled";
        };
    }

    @Override
    public int compareTo(@NotNull Ticket other) {
        return TICKET_COMPARATOR.compare(this, other);
    }
}
