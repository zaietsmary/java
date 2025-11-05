package ua.cinema.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.model.Actor;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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

    /**
     * Find subjects by partial name match (case-insensitive).
     *
     * @param firstName partial name to search for
     * @return immutable list of subjects matching the partial name
     */
    public List<Actor> findByFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            logger.warn("Attempted to search with null or empty partial name");
            return List.of();
        }

        String searchTerm = firstName.trim().toLowerCase();
        List<Actor> results = getAll().stream()
                .filter(name -> name.getFirstName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        logger.info("Found {} subjects containing '{}' in name", results.size(), firstName);
        return results;
    }

    /**
     * Find subjects within a credit range (inclusive).
     *
     * @param minBirthYear minimum credits (inclusive)
     * @param maxBirthYear maximum credits (inclusive)
     * @return immutable list of subjects within the credit range
     */
    public List<Actor> findByCreditsRange(int minBirthYear, int maxBirthYear) {
        if (minBirthYear > maxBirthYear) {
            logger.warn("Invalid credit range: min={} > max={}", minBirthYear, maxBirthYear);
            return List.of();
        }

        List<Actor> results = getAll().stream()
                .filter(subject -> subject.getBirthYear() >= minBirthYear && subject.getBirthYear() <= maxBirthYear)
                .collect(Collectors.toList());

        logger.info("Found {} subjects with credits between {} and {}", results.size(), minBirthYear, maxBirthYear);
        return results;
    }

    /**
     * Find subjects by difficulty level.
     *
     * @param lastName difficulty level ("Easy", "Medium", "Hard")
     * @return immutable list of subjects with the specified difficulty level
     */
    public List<Actor> findByLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            logger.warn("Attempted to search with null or empty difficulty level");
            return List.of();
        }

        List<Actor> results = getAll().stream()
                .filter(subject -> subject.getLastName().equalsIgnoreCase(lastName.trim()))
                .collect(Collectors.toList());

        logger.info("Found {} subjects with difficulty level '{}'", results.size(), lastName);
        return results;
    }

    /**
     * Find subjects with credits greater than or equal to specified value.
     *
     * @param birthYear minimum credits
     * @return immutable list of subjects with credits >= minCredits
     */
    public List<Actor> findByMinBirthYear(int birthYear) {
        List<Actor> results = getAll().stream()
                .filter(subject -> subject.getBirthYear() >= birthYear)
                .collect(Collectors.toList());

        logger.info("Found {} subjects with credits >= {}", results.size(), birthYear);
        return results;
    }


    /**
     * Get full names of all actors combined into one string.
     *
     * @return combined full names
     */
    public String getFullName() {
        String fullNames = getAll().stream()
                .map(actor -> actor.getFirstName() + " " + actor.getLastName())
                .reduce("", (a, b) -> a + (a.isEmpty() ? "" : ", ") + b);

        logger.info("All actor full names combined: {}", fullNames);
        return fullNames;
    }


    /**
     * Get all subjects with maximum credits.
     * If multiple subjects have the same maximum credit value, all are returned.
     *
     * @return unmodifiable list of subjects with max credits (empty if no subjects)
     */
    public List<Actor> getAllYoungestActors() {
        List<Actor> allActors = getAll();
        if (allActors.isEmpty()) {
            logger.info("No subjects found");
            return List.of();
        }

        int youngestActor = allActors.stream()
                .mapToInt(Actor::getBirthYear)
                .max()
                .orElse(0);

        List<Actor> results = allActors.stream()
                .filter(actor -> actor.getBirthYear() == youngestActor)
                .collect(Collectors.toList());

        logger.info("Found {} subject(s) with max credits: {} credits", results.size(), youngestActor);
        return results;
    }

    /**
     * Get one subject with maximum credits.
     * <p>
     * <strong>Note:</strong> If multiple subjects have the same maximum credit value,
     * one is returned arbitrarily (non-deterministic).
     * This method is primarily for demonstrating the reduce operation.
     * For production use, consider {@link #getAllSubjectsWithMaxCredits()} instead.
     * <p>
     *
     * @return Optional containing one subject with max credits, or empty if no subjects
     */
    /**
     * Find the actor with the shortest first name.
     *
     * @return Optional containing the actor with the shortest name, or empty if none found.
     */
    public Optional<Actor> getShortestName() {
        Optional<Actor> result = getAll().stream()
                .reduce((a1, a2) -> a1.getFirstName().length() < a2.getFirstName().length() ? a1 : a2);

        if (result.isPresent()) {
            Actor actor = result.get();
            logger.info("Actor with shortest name: {} {} ({} letters)",
                    actor.getFirstName(), actor.getLastName(), actor.getFirstName().length());
        } else {
            logger.info("No actors found");
        }

        return result;
    }


    /**
     * Group actors by birth year.
     *
     * @return Map of birth year to list of actors
     */
    public Map<Integer, List<Actor>> groupByBirthYear() {
        Map<Integer, List<Actor>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(Actor::getBirthYear));

        logger.info("Grouped actors by birth year: {} groups", grouped.size());
        grouped.forEach((year, actors) ->
                logger.debug("  {} - {} actors", year, actors.size())
        );

        return grouped;
    }

    /**
     * Get all full names in uppercase.
     *
     * @return list of actor full names in uppercase
     */
    public List<String> getAllNamesUpperCase() {
        List<String> names = getAll().stream()
                .map(Actor::getFullName)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        logger.info("Retrieved {} actor names in uppercase", names.size());
        return names;
    }

    /**
     * Count actors by birth year.
     *
     * @return Map of birth year to count
     */
    public Map<Integer, Long> countByBirthYear() {
        Map<Integer, Long> counts = getAll().stream()
                .collect(Collectors.groupingBy(
                        Actor::getBirthYear,
                        Collectors.counting()
                ));

        logger.info("Actor counts by birth year: {}", counts);
        return counts;
    }

    /**
     * Check if any actor was born in the given year.
     *
     * @param year birth year to check
     * @return true if any actor was born in the specified year
     */
    public boolean hasActorBornInYear(int year) {
        boolean exists = getAll().stream()
                .anyMatch(actor -> actor.getBirthYear() == year);

        logger.info("Actors born in {} exist: {}", year, exists);
        return exists;
    }

    /**
     * Check if all actors were born after the given year.
     *
     * @param minYear minimum birth year
     * @return true if all actors were born after minYear
     */
    public boolean allActorsBornAfter(int minYear) {
        boolean result = getAll().stream()
                .allMatch(actor -> actor.getBirthYear() >= minYear);

        logger.info("All actors born after {}: {}", minYear, result);
        return result;
    }

    class ActorRepository2 extends GenericRepositoryForInterface<Actor> {
        public ActorRepository2(String entityType) {
            super(entityType);
        }
    }
}
