package ua.cinema.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.model.Hall;

import java.util.Comparator;
import java.util.List;

public class HallRepository extends GenericRepository<Hall> {
    private static final Logger logger = LoggerFactory.getLogger(HallRepository.class);

    public HallRepository() {
        super(hall -> String.valueOf(hall.getHallNumber()), "Hall");
    }

    /**
     * Sort halls by hall number in descending order
     */
    public List<Hall> sortByHallNumberDesc() {
        List<Hall> allHalls = getAll();
        allHalls.sort(Comparator.comparingInt(Hall::getHallNumber).reversed());
        logger.info("Sorted {} by hall number in descending order", "Hall");
        return allHalls;
    }

    /**
     * Sort halls by hall number in ascending order
     */
    public List<Hall> sortByHallNumberAsc() {
        List<Hall> allHalls = getAll();
        allHalls.sort(Comparator.comparingInt(Hall::getHallNumber));
        logger.info("Sorted {} by hall number in ascending order", "Hall");
        return allHalls;
    }

    /**
     * Sort halls by capacity in descending order
     */
    public List<Hall> sortByCapacityDesc() {
        List<Hall> allHalls = getAll();
        allHalls.sort(Comparator.comparingInt(Hall::getCapacity).reversed());
        logger.info("Sorted {} by capacity in descending order", "Hall");
        return allHalls;
    }

    /**
     * Sort halls by capacity in ascending order
     */
    public List<Hall> sortByCapacityAsc() {
        List<Hall> allHalls = getAll();
        allHalls.sort(Comparator.comparingInt(Hall::getCapacity));
        logger.info("Sorted {} by capacity in ascending order", "Hall");
        return allHalls;
    }
}
