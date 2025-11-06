package ua.cinema.config;

/**
 * Константи ключів для доступу до налаштувань у config.properties
 */
public final class ConfigKeys {

    private ConfigKeys() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String DATA_PATH_BASE = "data.path.base";

    public static final String DATA_PATH_ACTORS_JSON = "data.path.actors.json";
    public static final String DATA_PATH_MOVIES_JSON = "data.path.movies.json";
    public static final String DATA_PATH_SCREENINGS_JSON = "data.path.screenings.json";
    public static final String DATA_PATH_HALLS_JSON = "data.path.halls.json";
    public static final String DATA_PATH_TICKETS_JSON = "data.path.tickets.json";

    public static final String DATA_PATH_ACTORS_YAML = "data.path.actors.yaml";
    public static final String DATA_PATH_MOVIES_YAML = "data.path.movies.yaml";
    public static final String DATA_PATH_SCREENINGS_YAML = "data.path.screenings.yaml";
    public static final String DATA_PATH_HALLS_YAML = "data.path.halls.yaml";
    public static final String DATA_PATH_TICKETS_YAML = "data.path.tickets.yaml";

    public static final String TEST_DATA_COUNT = "test.data.count";
}
