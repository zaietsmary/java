package ua.cinema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.config.AppConfig;
import ua.cinema.config.ConfigKeys;
import ua.cinema.model.*;
import ua.cinema.repository.TicketRepository;
import ua.cinema.serializer.JsonDataSerializer;
import ua.cinema.serializer.YamlDataSerializer;
import ua.cinema.exception.DataSerializationException;

import java.time.LocalDate;
import java.util.List;

public class MainSerialization {

    private static final Logger logger = LoggerFactory.getLogger(MainSerialization.class);

    public static void main(String[] args) {
        AppConfig config = new AppConfig();

        String ticketsJsonPath = config.getProperty(ConfigKeys.DATA_PATH_TICKETS_JSON, "data/tickets.json");
        String ticketsYamlPath = config.getProperty(ConfigKeys.DATA_PATH_TICKETS_YAML, "data/tickets.yaml");

        logger.info("📁 JSON file for tickets: {}", ticketsJsonPath);
        logger.info("📁 YAML file for tickets: {}", ticketsYamlPath);

        Movie inception = new Movie("Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16));
        Movie interstellar = new Movie("Interstellar", Genre.ACTION, 169, LocalDate.of(2014, 11, 7));
        Movie soul = new Movie("Soul", Genre.COMEDY, 100, LocalDate.of(2020, 12, 25));

        Hall hall1 = new Hall(1, 120);
        Hall hall2 = new Hall(2, 200);

        Screening screening1 = new Screening(inception, hall1, LocalDate.of(2025, 11, 10));
        Screening screening2 = new Screening(interstellar, hall2, LocalDate.of(2025, 11, 11));
        Screening screening3 = new Screening(soul, hall1, LocalDate.of(2025, 11, 12));

        Ticket t1 = new Ticket(screening1, 1, 150.0, TicketStatus.AVAILABLE);
        Ticket t2 = new Ticket(screening1, 2, 150.0, TicketStatus.SOLD);
        Ticket t3 = new Ticket(screening2, 1, 180.0, TicketStatus.RESERVED);
        Ticket t4 = new Ticket(screening2, 2, 180.0, TicketStatus.AVAILABLE);
        Ticket t5 = new Ticket(screening3, 1, 200.0, TicketStatus.CANCELED);

        TicketRepository ticketRepository = new TicketRepository();
        ticketRepository.add(t1);
        ticketRepository.add(t2);
        ticketRepository.add(t3);
        ticketRepository.add(t4);
        ticketRepository.add(t5);

        JsonDataSerializer<Ticket> jsonSerializer = new JsonDataSerializer<>();
        YamlDataSerializer<Ticket> yamlSerializer = new YamlDataSerializer<>();

        try {
            jsonSerializer.serialize(ticketRepository.getAll(), ticketsJsonPath);
            yamlSerializer.serialize(ticketRepository.getAll(), ticketsYamlPath);
            logger.info("! Tickets successfully serialized!");
        } catch (DataSerializationException e) {
            logger.error("! Serialization error: {}", e.getMessage(), e);
        }

        TicketRepository ticketRepoFromJson = new TicketRepository();
        TicketRepository ticketRepoFromYaml = new TicketRepository();

        try {
            List<Ticket> ticketsFromJson = jsonSerializer.deserialize(ticketsJsonPath, Ticket.class);
            ticketsFromJson.forEach(ticketRepoFromJson::add);

            List<Ticket> ticketsFromYaml = yamlSerializer.deserialize(ticketsYamlPath, Ticket.class);
            ticketsFromYaml.forEach(ticketRepoFromYaml::add);

            logger.info("! Tickets successfully deserialized!");
        } catch (DataSerializationException e) {
            logger.error("! Deserialization error: {}", e.getMessage(), e);
        }

        logger.info("Original tickets count: {}", ticketRepository.size());
        logger.info("Tickets from JSON count: {}", ticketRepoFromJson.size());
        logger.info("Tickets from YAML count: {}", ticketRepoFromYaml.size());

        ticketRepoFromJson.groupByGenre().forEach((genre, list) ->
                logger.info("Genre: {}, Tickets: {}", genre, list.size())
        );

        ticketRepoFromJson.countByStatus().forEach((status, count) ->
                logger.info("Status: {}, Count: {}", status, count)
        );
    }
}
