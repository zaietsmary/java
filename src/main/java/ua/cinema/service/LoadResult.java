package ua.cinema.service;

/**
 * Result of data loading operation (Two entities + Duration)
 */
public record LoadResult(
        int ticketsLoaded,
        int actorsLoaded,
        long durationMs
) {
    public int getTotalItems() {
        // Сума лише оголошених полів
        return ticketsLoaded + actorsLoaded;
    }

    @Override
    public String toString() {
        return String.format(
                "LoadResult{tickets=%d, actors=%d, total=%d, duration=%dms}",
                ticketsLoaded, actorsLoaded,
                getTotalItems(), durationMs
        );
    }
}