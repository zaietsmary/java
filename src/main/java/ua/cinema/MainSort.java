package ua.cinema;

import ua.cinema.model.Actor;
import ua.cinema.repository.ActorRepository;

import java.util.List;

public class MainSort {

    public static void main(String[] args) {
        ActorRepository repository = new ActorRepository();

        Actor actorAlice = new Actor("Alice", "Smith", 1985);
        Actor actorBob = new Actor("Bob", "Johnson", 1983);
        Actor actorCharlie = new Actor("Charlie", "Brown", 1990);
        Actor actorAliceB = new Actor("Alice", "Williams", 1987);
        Actor actorAnna = new Actor("Anna", "Williams", 1992);

        repository.add(actorAlice);
        repository.add(actorBob);
        repository.add(actorCharlie);
        repository.add(actorAliceB);
        repository.add(actorAnna);

        System.out.println("=== Sorted by Name (asc) ===");
        List<Actor> sortedByName = repository.sortByName();
        sortedByName.forEach(System.out::println);

        System.out.println("\n=== Sorted by Name (desc) ===");
        List<Actor> sortedByNameDesc = repository.sortByNameDesc();
        sortedByNameDesc.forEach(System.out::println);

        System.out.println("\n=== Sorted by Birth Year ===");
        List<Actor> sortedByBirthYear = repository.sortByBirthYear();
        sortedByBirthYear.forEach(System.out::println);

        System.out.println("\n=== Sorted by Identity (desc) ===");
        repository.sortByIdentity(false);
        repository.getAll().forEach(System.out::println);
    }

}
