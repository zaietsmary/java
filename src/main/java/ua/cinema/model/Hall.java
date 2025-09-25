package ua.cinema.model;

import java.util.Objects;

import ua.cinema.util.HallUtils;

public class Hall {
    private int hallNumber;
    private int capacity;

    public Hall() {
    }

    public Hall(int hallNumber, int capacity) {
        if(!HallUtils.isValidHallNumber(hallNumber)){
            throw new IllegalArgumentException("Invalid hall number: " + hallNumber);
        }
        if(!HallUtils.isValidCapacity(capacity)){
            throw new IllegalArgumentException("Invalid capacity: " + capacity);
        }
        this.hallNumber = hallNumber;
        this.capacity = capacity;
    }

    public int getHallNumber() {
        if(!HallUtils.isValidHallNumber(hallNumber)){
            throw new IllegalStateException("Invalid hall number: " + hallNumber);
        }
        return hallNumber;
    }

    public void setHallNumber(int hallNumber) {
        if(!HallUtils.isValidHallNumber(hallNumber)){
            throw new IllegalArgumentException("Invalid hall number: " + hallNumber);
        }
        this.hallNumber = hallNumber;
    }

    public int getCapacity() {
        if(!HallUtils.isValidCapacity(capacity)){
            throw new IllegalStateException("Invalid capacity: " + capacity);
        }
        return capacity;
    }

    public void setCapacity(int capacity) {
        if(!HallUtils.isValidCapacity(capacity)){
            throw new IllegalArgumentException("Invalid capacity: " + capacity);
        }
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Hall{" +
                "hallNumber=" + hallNumber +
                ", capacity=" + capacity +
                '}';
    }

    public String getHallInfo() {
        return "Hall " + hallNumber + " (Capacity: " + capacity + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hall hall = (Hall) o;
        return hallNumber == hall.hallNumber &&
                capacity == hall.capacity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hallNumber, capacity);
    }

    public static Hall of(int hallNumber, int capacity) {
        return new Hall(hallNumber, capacity);
    }
}
