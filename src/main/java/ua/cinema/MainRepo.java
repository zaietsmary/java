package ua.cinema;

import ua.cinema.model.Hall;
import ua.cinema.model.Actor;
import ua.cinema.repository.GenericRepository;
import ua.cinema.repository.IdentityExtractor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainRepo {

    private static final Logger logger = Logger.getLogger(MainRepo.class.getName());

    public static void main(String[] args) {
        logger.setLevel(Level.INFO);

        logger.info("=== ПОЧАТОК РОБОТИ ПРОГРАМИ ===");

        logger.info("Створення репозиторію Hall...");
        IdentityExtractor<Hall> hallIdExtractor = hall -> String.valueOf(hall.getHallNumber());
        GenericRepository<Hall> hallRepo = new GenericRepository<>(hallIdExtractor, "Hall");

        Hall hall1 = new Hall(5, 60);
        Hall hall2 = new Hall(6, 60);
        Hall hall3 = new Hall(7, 60);
        Hall hallDuplicate = new Hall(6, 100);

        logger.info("Додаємо зали...");
        hallRepo.add(hall1);
        hallRepo.add(hall2);
        hallRepo.add(hall3);
        hallRepo.add(hallDuplicate);

        logger.info("Поточні зали у репозиторії: " + hallRepo.getAll());

        logger.info("Видаляємо зал №5...");
        hallRepo.remove(hall1);

        logger.info("Пошук залу з номером 6:");
        hallRepo.findByIdentity("6")
                .ifPresentOrElse(
                        hall -> logger.info("Знайдено зал: " + hall),
                        () -> logger.warning(" Зал не знайдено")
                );

        logger.info("Пошук залу з номером 63:");
        hallRepo.findByIdentity("63")
                .ifPresentOrElse(
                        hall -> logger.info("Знайдено зал: " + hall),
                        () -> logger.warning("Немає залу з таким номером")
                );


        logger.info("Створення репозиторію Actor...");
        IdentityExtractor<Actor> actorExtractor = Actor::getFirstName;
        GenericRepository<Actor> actorRepo = new GenericRepository<>(actorExtractor, "Actor");

        Actor actor1 = new Actor("Tom", "Hanks", 1956);
        Actor actor2 = new Actor("Natalie", "Portman", 1981);
        Actor actor3 = new Actor("Tom", "Cruise", 1962); // дубль за firstName

        logger.info("Додаємо акторів...");
        actorRepo.add(actor1);
        actorRepo.add(actor2);
        actorRepo.add(actor3);

        logger.info("Поточні актори у репозиторії: " + actorRepo.getAll());

        logger.info("Пошук актора за ім’ям 'Tom':");
        actorRepo.findByIdentity("Tom")
                .ifPresentOrElse(
                        actor -> logger.info("Знайдено актора: " + actor),
                        () -> logger.warning("Актор не знайдений")
                );

        logger.info("=== КІНЕЦЬ РОБОТИ ПРОГРАМИ ===");
    }
}
