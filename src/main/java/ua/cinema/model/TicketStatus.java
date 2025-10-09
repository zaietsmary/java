package ua.cinema.model;

import ua.cinema.exception.InvalidDataException;
import java.util.logging.Logger;

public enum TicketStatus {
    AVAILABLE("Квиток доступний для покупки"),
    RESERVED("Квиток заброньовано"),
    SOLD("Квиток продано"),
    CANCELED("Квиток скасовано");

    private static final Logger logger = Logger.getLogger(TicketStatus.class.getName());

    private final String description;

    TicketStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinal() {
        return this == SOLD || this == CANCELED;
    }

    public static TicketStatus parseStatus(String value) throws InvalidDataException {
        logger.info("Attempting to parse TicketStatus from value: " + value);

        if (value == null || value.isBlank()) {
            logger.warning("Attempted to parse null or blank TicketStatus value");
            throw new InvalidDataException("TicketStatus value cannot be null or blank");
        }

        try {
            TicketStatus status = TicketStatus.valueOf(value.toUpperCase().trim());
            logger.info("Successfully parsed TicketStatus: " + status.name());
            return status;
        } catch (IllegalArgumentException e) {
            logger.severe("Failed to parse TicketStatus: '" + value + "' - invalid value");
            throw new InvalidDataException("Invalid TicketStatus: '" + value + "'. Should be one of: " +
                    java.util.Arrays.toString(TicketStatus.values()), e);
        }
    }
}
