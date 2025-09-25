package ua.cinema.model;

import java.util.Objects;

import ua.cinema.util.TicketUtils;

public class Ticket {
    private Screening screening;
    private int seatNumber;
    private double price;

    public Ticket() {
    }

    public Ticket(Screening screening, int seatNumber, double price) {
        if(!TicketUtils.isValidScreening(screening)){
            throw new IllegalArgumentException("Invalid screening: " + screening);
        }
        if(!TicketUtils.isValidSeatNumber(seatNumber)){
            throw new IllegalArgumentException("Invalid seat number: " + seatNumber);
        }
        if(!TicketUtils.isValidPrice(price)){
            throw new IllegalArgumentException("Invalid price: " + price);
        }
        this.screening = screening;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public Screening getScreening() {
        if(!TicketUtils.isValidScreening(screening)){
            throw new IllegalStateException("Invalid screening: " + screening);
        }
        return screening;
    }

    public void setScreening(Screening screening) {
        if(!TicketUtils.isValidScreening(screening)){
            throw new IllegalArgumentException("Invalid screening: " + screening);
        }
        this.screening = screening;
    }

    public int getSeatNumber() {
        if(!TicketUtils.isValidSeatNumber(seatNumber)){
            throw new IllegalStateException("Invalid seat number: " + seatNumber);
        }
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        if(!TicketUtils.isValidSeatNumber(seatNumber)){
            throw new IllegalArgumentException("Invalid seat number: " + seatNumber);
        }
        this.seatNumber = seatNumber;
    }

    public double getPrice() {
        if(!TicketUtils.isValidPrice(price)){
            throw new IllegalStateException("Invalid price: " + price);
        }
        return price;
    }

    public void setPrice(double price) {
        if(!TicketUtils.isValidPrice(price)){
            throw new IllegalArgumentException("Invalid price: " + price);
        }
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "screening=" + screening +
                ", seatNumber=" + seatNumber +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return seatNumber == ticket.seatNumber &&
                Double.compare(ticket.price, price) == 0 &&
                screening.equals(ticket.screening);
    }

    @Override
    public int hashCode() {
        return Objects.hash(screening, seatNumber, price);
    }

    public static Ticket of(Screening screening, int seatNumber, double price) {
        return new Ticket(screening, seatNumber, price);
    }
}
