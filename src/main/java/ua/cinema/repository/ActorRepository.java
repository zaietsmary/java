package ua.cinema.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.model.Actor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ActorRepository extends GenericRepository<Actor> {
    private static final Logger logger = LoggerFactory.getLogger(ActorRepository.class);

    public ActorRepository() {
        super(actor -> actor.getFirstName() + " " + actor.getLastName() + " (" + actor.getBirthYear() + ")", "Actor");
    }

    public ActorRepository(IdentityExtractor<Actor> identityExtractor) {
        super(identityExtractor, "Actor");
    }

    /**
     * Sort actors by last name, first name, then birth year.
     * Returns a new list, original repository is not modified.
     */
    public List<Actor> sortByName() {
        List<Actor> sortedList = new ArrayList<>(getAll()); // копія
        sortedList.sort(
                Comparator.comparing(Actor::getLastName)
                        .thenComparing(Actor::getFirstName)
                        .thenComparingInt(Actor::getBirthYear)
        );
        logger.info("Sorted {} by last name, first name, birth year in ascending order", "Actor");
        return sortedList;
    }

    /**
     * Sort actors by last name descending, then first name, then birth year.
     * Returns a new list, original repository is not modified.
     */
    public List<Actor> sortByNameDesc() {
        List<Actor> sortedList = new ArrayList<>(getAll()); // копія
        sortedList.sort(
                Comparator.comparing(Actor::getLastName).reversed()
                        .thenComparing(Actor::getFirstName)
                        .thenComparingInt(Actor::getBirthYear)
        );
        logger.info("Sorted {} by last name (desc), first name, birth year in ascending order", "Actor");
        return sortedList;
    }

    /**
     * Sort actors by birth year, then last name, then first name.
     * Returns a new list, original repository is not modified.
     */
    public List<Actor> sortByBirthYear() {
        List<Actor> sortedList = new ArrayList<>(getAll()); // копія
        sortedList.sort(
                Comparator.comparingInt(Actor::getBirthYear)
                        .thenComparing(Actor::getLastName)
                        .thenComparing(Actor::getFirstName)
        );
        logger.info("Sorted {} by birth year, last name, and first name", "Actor");
        return sortedList;
    }

    class ActorRepository2 extends GenericRepositoryForInterface<Actor> {
        public ActorRepository2(String entityType) {
            super(entityType);
        }
    }
}
