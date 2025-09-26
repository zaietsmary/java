package ua.cinema.model;

import ua.cinema.util.TicketUtils;

public record Ticket(Screening screening, int seatNumber, double price, TicketStatus ticketStatus) {

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
}
