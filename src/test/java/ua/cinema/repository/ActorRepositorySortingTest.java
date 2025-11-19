package ua.cinema.repository;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.model.Actor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ActorRepository Sorting Tests")
class ActorRepositorySortingTest {
    private static final Logger logger = LoggerFactory.getLogger(ActorRepositorySortingTest.class);

    private ActorRepository actorRepository;
    private Actor actorAlice;
    private Actor actorBob;
    private Actor actorCharlie;
    private Actor actorAliceB;
    private Actor actorAliceA;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data");
        actorRepository = new ActorRepository();

        actorAlice = new Actor("Alice", "Smith", 1985);
        actorBob = new Actor("Bob", "Johnson", 1983);
        actorCharlie = new Actor("Charlie", "Brown", 1990);

        actorAliceB = new Actor("Alice", "Williams", 1987);
        actorAliceA = new Actor("Anna", "Williams", 1992);

        actorRepository.add(actorBob);
        actorRepository.add(actorAlice);
        actorRepository.add(actorCharlie);
        actorRepository.add(actorAliceB);
        actorRepository.add(actorAliceA);

        logger.info("Test setup completed with {} actors", actorRepository.getAll().size());
    }

    @Test
    @DisplayName("sortByName should sort actors by last name, first name, then birth year")
    void testSortByName() {
        logger.info("Testing sortByName");

        List<Actor> sorted = actorRepository.sortByName();

        assertThat(sorted)
                .as("Should return all actors")
                .hasSize(5);

        SoftAssertions softly = new SoftAssertions();

        // Brown < Johnson < Smith < Williams (Alice) < Williams (Anna)
        softly.assertThat(sorted.get(0).getLastName()).isEqualTo("Brown");
        softly.assertThat(sorted.get(1).getLastName()).isEqualTo("Johnson");
        softly.assertThat(sorted.get(2).getLastName()).isEqualTo("Smith");
        softly.assertThat(sorted.get(3).getLastName()).isEqualTo("Williams");
        softly.assertThat(sorted.get(3).getFirstName()).isEqualTo("Alice");
        softly.assertThat(sorted.get(4).getLastName()).isEqualTo("Williams");
        softly.assertThat(sorted.get(4).getFirstName()).isEqualTo("Anna");

        softly.assertAll();
        logger.info("sortByName test completed successfully");
    }

    @Test
    @DisplayName("sortByNameDesc should sort actors by last name descending, first name, birth year")
    void testSortByNameDesc() {
        logger.info("Testing sortByNameDesc");

        List<Actor> sorted = actorRepository.sortByNameDesc();

        assertThat(sorted).hasSize(5);

        SoftAssertions softly = new SoftAssertions();

        // Williams > Smith > Johnson > Brown
        softly.assertThat(sorted.get(0).getLastName()).isEqualTo("Williams");
        softly.assertThat(sorted.get(0).getFirstName()).isEqualTo("Alice");
        softly.assertThat(sorted.get(1).getLastName()).isEqualTo("Williams");
        softly.assertThat(sorted.get(1).getFirstName()).isEqualTo("Anna");
        softly.assertThat(sorted.get(2).getLastName()).isEqualTo("Smith");
        softly.assertThat(sorted.get(3).getLastName()).isEqualTo("Johnson");
        softly.assertThat(sorted.get(4).getLastName()).isEqualTo("Brown");

        softly.assertAll();
        logger.info("sortByNameDesc test completed successfully");
    }

    @Test
    @DisplayName("sortByBirthYear should sort actors by birth year, then last name, first name")
    void testSortByBirthYear() {
        logger.info("Testing sortByBirthYear");

        List<Actor> sorted = actorRepository.sortByBirthYear();

        assertThat(sorted).hasSize(5);

        SoftAssertions softly = new SoftAssertions();

        // Expected order by birth year: Bob(1983), Alice(1985), AliceB(1987), Charlie(1990), Anna(1992)
        softly.assertThat(sorted.get(0).getFirstName()).isEqualTo("Bob");
        softly.assertThat(sorted.get(1).getFirstName()).isEqualTo("Alice");
        softly.assertThat(sorted.get(2).getFirstName()).isEqualTo("Alice");
        softly.assertThat(sorted.get(3).getFirstName()).isEqualTo("Charlie");
        softly.assertThat(sorted.get(4).getFirstName()).isEqualTo("Anna");

        softly.assertAll();
        logger.info("sortByBirthYear test completed successfully");
    }

    @Test
    @DisplayName("sortByName should not modify the original repository")
    void testSortByNameDoesNotModifyRepository() {
        logger.info("Testing that sortByName does not modify repository");

        List<Actor> originalOrder = actorRepository.getAll();
        List<Actor> sorted = actorRepository.sortByName();
        List<Actor> currentOrder = actorRepository.getAll();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(currentOrder).isEqualTo(originalOrder);
        softly.assertThat(sorted).isNotSameAs(currentOrder);

        softly.assertAll();
    }

    @Test
    @DisplayName("Sorting empty repository should return empty list")
    void testSortingEmptyRepository() {
        logger.info("Testing sorting on empty repository");

        ActorRepository emptyRepo = new ActorRepository();
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(emptyRepo.sortByName()).isEmpty();
        softly.assertThat(emptyRepo.sortByNameDesc()).isEmpty();
        softly.assertThat(emptyRepo.sortByBirthYear()).isEmpty();

        softly.assertAll();
        logger.info("Empty repository sorting test completed");
    }

    @Test
    @DisplayName("Sorting single actor should return list with that actor")
    void testSortingSingleActor() {
        logger.info("Testing sorting with single actor");

        ActorRepository singleRepo = new ActorRepository();
        Actor actor = new Actor("Alice", "Smith", 1985);
        singleRepo.add(actor);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(singleRepo.sortByName()).containsExactly(actor);
        softly.assertThat(singleRepo.sortByNameDesc()).containsExactly(actor);
        softly.assertThat(singleRepo.sortByBirthYear()).containsExactly(actor);

        softly.assertAll();
        logger.info("Single actor sorting test completed");
    }
}
