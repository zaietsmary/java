package ua.cinema.model;

import org.jetbrains.annotations.NotNull;
import ua.cinema.util.TicketUtils;

import java.util.Comparator;

public record Ticket(Screening screening, int seatNumber, double price, TicketStatus ticketStatus) implements Comparable<Ticket> {

    public static final Comparator<Ticket> TIECKET_COMPARATOR =
            Comparator.comparing(Ticket::screening)
                    .thenComparing(Ticket::seatNumber)
                    .thenComparing(Ticket::price)
                    .thenComparing(Ticket::ticketStatus);

    public Ticket {
        if(!TicketUtils.isValidScreening(screening)){
            throw new IllegalArgumentException("Invalid screening: " + screening);
        }
        if(!TicketUtils.isValidSeatNumber(seatNumber)){
            throw new IllegalArgumentException("Invalid seat number: " + seatNumber);
        }
        if(!TicketUtils.isValidPrice(price)) {
            throw new IllegalArgumentException("Invalid price: " + price);
        }
        if(!TicketUtils.isValidTicketStatus(ticketStatus)){
            throw new IllegalArgumentException("Invalid ticket status: " + ticketStatus);
        }
    }

    public static String getTicketStatus(TicketStatus ticketStatus) {
        switch (ticketStatus) {
            case RESERVED: return "Reserved";
            case AVAILABLE: return "Available";
            case SOLD: return "Sold";
            case CANCELED: return "Canceled";
            default: throw new IllegalArgumentException("Invalid ticket status: " + ticketStatus);
        }
    }

    public static Ticket of(Screening screening, int seatNumber, double price, TicketStatus ticketStatus) {
        return new Ticket(screening, seatNumber, price, ticketStatus);
    }

    @Override
    public int compareTo(@NotNull Ticket other) {
        return TIECKET_COMPARATOR.compare(this, other);
    }
}