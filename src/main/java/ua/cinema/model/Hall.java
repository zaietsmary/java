package ua.cinema.model;

import java.util.Objects;

import ua.cinema.util.HallUtils;

public record Hall(int hallNumber, int capacity) {

    public Hall {
        if(!HallUtils.isValidHallNumber(hallNumber)){
            throw new IllegalArgumentException("Invalid hall number: " + hallNumber);
        }
        if(!HallUtils.isValidCapacity(capacity)){
            throw new IllegalArgumentException("Invalid capacity: " + capacity);
        }
    }

    public String getHallInfo() {
        return "Hall " + hallNumber + " (Capacity: " + capacity + ")";
    }

    public static Hall of(int hallNumber, int capacity) {
        return new Hall(hallNumber, capacity);
    }
}
