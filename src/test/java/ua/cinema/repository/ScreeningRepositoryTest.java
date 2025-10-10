package ua.cinema.repository;

import org.junit.jupiter.api.*;
import org.assertj.core.api.SoftAssertions;
import ua.cinema.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GenericRepository<Screening>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Screening Repository Tests")
class ScreeningRepositoryTest {

    private GenericRepository<Screening> screeningRepository;
    private Movie testMovie1, testMovie2;
    private Hall testHall1, testHall2, testHall3;
    private Screening testScreening1, testScreening2, testScreening3;

    @BeforeAll
    void setUpTestData() {
        // Movies
        testMovie1 = new Movie("Inception", Genre.ACTION, 148, LocalDate.of(2010, 7, 16));
        testMovie2 = new Movie("Interstellar", Genre.ACTION, 169, LocalDate.of(2014, 11, 7));

        // Halls
        testHall1 = new Hall(1, 100);
        testHall2 = new Hall(2, 150);
        testHall3 = new Hall(3, 80);

        // Screenings
        testScreening1 = new Screening(testMovie1, testHall1, LocalDate.now().plusDays(1));
        testScreening2 = new Screening(testMovie1, testHall2, LocalDate.now().plusDays(2));
        testScreening3 = new Screening(testMovie2, testHall3, LocalDate.now().plusDays(3));
    }

    @BeforeEach
    void setUp() {
        screeningRepository = new GenericRepository<>(s -> s.getMovie().title(), "Movie");
        screeningRepository.getItemsForTesting().add(testScreening1);
    }

    @Test
    @DisplayName("Add screenings")
    void testAddScreenings() {
        SoftAssertions softly = new SoftAssertions();

        int initialSize = screeningRepository.size();

        softly.assertThat(screeningRepository.add(testScreening2)).as("Add testScreening2").isTrue();
        softly.assertThat(screeningRepository.add(testScreening3)).as("Add testScreening3").isTrue();
        softly.assertThat(screeningRepository.size()).as("Repository size should be 3").isEqualTo(initialSize + 2);

        softly.assertAll();
    }

    @Test
    @DisplayName("Prevent duplicate screenings by movie title")
    void testDuplicatePrevention() {
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(screeningRepository.add(testScreening1)).as("Duplicate testScreening1 should fail").isFalse();
        softly.assertThat(screeningRepository.size()).as("Size should remain 1").isEqualTo(1);

        softly.assertAll();
    }

    @Test
    @DisplayName("Find existing screening")
    void testFindExistingScreening() {
        Optional<Screening> result = screeningRepository.findByIdentity(testScreening1.getMovie().title());
        assertTrue(result.isPresent(), "Should find testScreening1");
    }

    @Test
    @DisplayName("Find non-existing screening")
    void testFindNonExistingScreening() {
        Optional<Screening> result = screeningRepository.findByIdentity("Non-existent movie");
        assertFalse(result.isPresent(), "Should not find non-existent screening");
    }

    @Test
    @DisplayName("Remove existing screening")
    void testRemoveExistingScreening() {
        int initialSize = screeningRepository.size();
        boolean removed = screeningRepository.removeByIdentity(testScreening1.getMovie().title());

        assertTrue(removed, "Should remove existing screening");
        assertEquals(initialSize - 1, screeningRepository.size(), "Size decreases by 1");
    }

    @Test
    @DisplayName("Remove non-existing screening")
    void testRemoveNonExistingScreening() {
        int initialSize = screeningRepository.size();
        boolean removed = screeningRepository.removeByIdentity("Unknown");
        assertFalse(removed, "Should not remove non-existent screening");
        assertEquals(initialSize, screeningRepository.size(), "Size remains unchanged");
    }

    @Test
    @DisplayName("Remove null identity")
    void testRemoveNullIdentity() {
        int initialSize = screeningRepository.size();
        boolean removed = screeningRepository.removeByIdentity(null);
        assertFalse(removed, "Should not remove null identity");
        assertEquals(initialSize, screeningRepository.size(), "Size remains unchanged");
    }

    @Test
    @DisplayName("Clear repository")
    void testClearRepository() {
        screeningRepository.add(testScreening2);
        screeningRepository.add(testScreening3);

        assertEquals(3, screeningRepository.size(), "Size before clear is 3");

        screeningRepository.clear();
        assertEquals(0, screeningRepository.size(), "Size after clear is 0");
        assertTrue(screeningRepository.isEmpty(), "Repository is empty after clear");
    }

    @Test
    @DisplayName("Get all screenings")
    void testGetAllScreenings() {
        screeningRepository.add(testScreening2);
        screeningRepository.add(testScreening3);

        List<Screening> all = screeningRepository.getAll();
        assertEquals(3, all.size(), "Should return all screenings");
        assertTrue(all.contains(testScreening1));
        assertTrue(all.contains(testScreening2));
        assertTrue(all.contains(testScreening3));

        all.clear();
        assertEquals(3, screeningRepository.size(), "Repository size should remain unchanged");
    }
}
