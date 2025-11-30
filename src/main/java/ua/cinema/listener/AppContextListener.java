package ua.cinema.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cinema.config.AppConfig;
import ua.cinema.model.Actor;
import ua.cinema.model.Ticket;
import ua.cinema.persistence.PersistenceManager;
import ua.cinema.repository.ActorRepository;
import ua.cinema.repository.TicketRepository;
import ua.cinema.service.LoadResult;
import ua.cinema.service.loading.DataLoader;
import ua.cinema.service.loading.ExecutorLoadingStrategy;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    private long startTime;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        startTime = System.currentTimeMillis();
        logger.info("=== APPLICATION STARTUP ===");

        ServletContext context = sce.getServletContext();

        try {
            // Create config & persistence
            AppConfig config = new AppConfig();
            PersistenceManager persistenceManager = new PersistenceManager(config);

            // Create repositories
            ActorRepository actorRepository = new ActorRepository();
            TicketRepository ticketRepository = new TicketRepository();

            // Load JSON data
            DataLoader loader = new DataLoader(persistenceManager);
            LoadResult result = loader.load(
                    actorRepository,
                    ticketRepository,
                    new ExecutorLoadingStrategy(4)
            );

            logLoadResults(result, actorRepository, ticketRepository);

            // Store in ServletContext (Singleton)
            context.setAttribute("actorRepository", actorRepository);
            context.setAttribute("ticketRepository", ticketRepository);
            context.setAttribute("persistenceManager", persistenceManager);

            logger.info("=== STARTUP COMPLETE ===");

        } catch (Exception e) {
            logger.error("Error during startup!", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== APPLICATION SHUTDOWN ===");

        ServletContext ctx = sce.getServletContext();

        try {
            ActorRepository actorRepo = (ActorRepository) ctx.getAttribute("actorRepository");
            TicketRepository ticketRepo = (TicketRepository) ctx.getAttribute("ticketRepository");
            PersistenceManager pm = (PersistenceManager) ctx.getAttribute("persistenceManager");

            logger.info("Saving data before shutdown...");

            if (pm != null) {

                if (actorRepo != null) {
                    pm.save(actorRepo.getAll(), "actors", Actor.class, "JSON");
                }

                if (ticketRepo != null) {
                    pm.save(ticketRepo.getAll(), "tickets", Ticket.class, "JSON");
                }
            }

            logger.info("All data saved successfully");

            // Remove context attributes
            ctx.removeAttribute("actorRepository");
            ctx.removeAttribute("ticketRepository");
            ctx.removeAttribute("persistenceManager");

        } catch (Exception e) {
            logger.error("Error during shutdown!", e);
        }
    }

    private void logLoadResults(LoadResult result,
                                ActorRepository actorRepo,
                                TicketRepository ticketRepo) {

        logger.info("=== DATA LOADED ===");
        logger.info("Actors loaded: {}", actorRepo.size());
        logger.info("Tickets loaded: {}", ticketRepo.size());
        logger.info("Total: {}", actorRepo.size() + ticketRepo.size());
    }
}
