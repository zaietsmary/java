package ua.cinema.service.loading;

import ua.cinema.repository.*;
import ua.cinema.service.LoadResult;

@FunctionalInterface
public interface LoadingStrategy {

    LoadResult load(
            TicketRepository ticketRepository,
            ActorRepository actorRepository,
            DataLoader dataLoader
    );
}