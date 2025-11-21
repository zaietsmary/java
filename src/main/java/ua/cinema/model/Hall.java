package ua.cinema.model;

import java.util.Comparator;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ua.cinema.exception.InvalidDataException;
import ua.cinema.util.ValidationUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Hall implements Comparable<Hall> {

    private static final Logger logger = LoggerFactory.getLogger(Hall.class);

    @Min(value = 1, message = "hallNumber must be >= 1")
    @Max(value = 10, message = "hallNumber cannot be bigger than 10")
    private int hallNumber;
    @Min(value = 1, message = "capacity must be >= 1")
    @Max(value = 100, message = "capacity cannot be bigger than 100")
    private int capacity;

    private static final Comparator<Hall> HALL_COMPARATOR =
            Comparator.comparing(Hall::getHallNumber)
            .thenComparing(Hall::getCapacity);

    public Hall( @JsonProperty("hallNumber") int hallNumber,
                 @JsonProperty("capacity") int capacity) {
        logger.debug("Attempting to create Hall: hallNumber={}, capacity={}", hallNumber, capacity);
        this.hallNumber = hallNumber;
        this.capacity = capacity;

        try {
            ValidationUtils.validate(this);
            logger.info("Hall successfully created: {}", this);
        } catch (InvalidDataException e) {
            logger.error("Validation failed for Hall: {}", e.getMessage());
            throw e;
        }
    }

    public Hall() {}

    public static Hall createHall(int hallNumber, int capacity) {
        try {
            Hall hall = new Hall(hallNumber, capacity);
            return hall;
        } catch (InvalidDataException e) {
            logger.error("Factory: failed to create Hall: {}", e.getMessage());
            throw e;
        }
    }


    public int getHallNumber() {
        return hallNumber;
    }

    public void setHallNumber(int hallNumber) {
        int old = this.hallNumber;
        this.hallNumber = hallNumber;

        try {
            ValidationUtils.validate(this);
            logger.info("Set hall number='{}'", this.hallNumber);
        } catch (InvalidDataException e) {
            this.hallNumber = old;
            logger.error("Invalid hallNumber, rollback to previous value='{}'", this.hallNumber, e);
            throw e;
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        int old = this.capacity;
        this.capacity = capacity;
        try {
            ValidationUtils.validate(this);
            logger.info("Set capacity='{}'", this.capacity);
        } catch (InvalidDataException e) {
            this.capacity = old;
            logger.error("Invalid capacity: {}", capacity, e);
            throw e;
        }
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

    @Override
    public int compareTo(@NotNull Hall other) {
        return HALL_COMPARATOR.compare(this, other);
    }
}
