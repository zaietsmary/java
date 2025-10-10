package ua.cinema;

import ua.cinema.exception.InvalidDataException;
import ua.cinema.model.Hall;
import ua.cinema.model.Movie;
import ua.cinema.parser.HallFileParser;
import ua.cinema.parser.MovieFileParser;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainDemo {
    private static final Logger logger = Logger.getLogger(MainDemo.class.getName());

    public static void main(String[] args) {
        String hallsFile = "D:\\untitled1\\src\\halls.csv";
        String moviesFile = "D:\\untitled1\\src\\halls.csv";

        try {
            List<Hall> halls = HallFileParser.parseFromCSV(hallsFile);
            for (Hall hall : halls) {
                logger.log(Level.INFO, "Created Hall object: {0}", hall.getHallInfo());
            }

            List<Movie> movies = MovieFileParser.parseFromCSV(moviesFile);
            for (Movie movie : movies) {
                logger.log(Level.INFO, "Created Movie object: {0}", movie.title());
            }

        } catch (InvalidDataException | IOException e) {
            logger.log(Level.SEVERE, "Error loading data: " + e.getMessage(), e);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error: " + e.getMessage(), e);

        } finally {
            logger.log(Level.INFO, "Demo execution finished.");
            System.out.println("Demo finished execution.");
        }
    }
}
