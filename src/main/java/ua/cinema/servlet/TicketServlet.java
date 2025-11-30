package ua.cinema.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cinema.exception.AlreadyExistsException;
import ua.cinema.exception.DataSerializationException;
import ua.cinema.exception.InvalidDataException;
import ua.cinema.model.Ticket;
import ua.cinema.repository.TicketRepository;
import ua.cinema.serializer.JsonDataSerializer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * REST API Servlet for Ticket CRUD operations and complex queries.
 */
@WebServlet(name = "TicketServlet", urlPatterns = {"/tickets", "/tickets/*"})
public class TicketServlet extends BaseServlet {
    // Поля було перейменовано
    private JsonDataSerializer<Ticket> ticketSerializer;
    private JsonDataSerializer<Object> generalSerializer;
    private TicketRepository ticketRepository;

    @Override
    public void init() throws ServletException {
        logger.info("=== TicketServlet init() ===");

        // Ініціалізація обох серіалізаторів
        ticketSerializer = new JsonDataSerializer<>();
        generalSerializer = new JsonDataSerializer<>();

        ticketRepository = (TicketRepository) getServletContext()
                .getAttribute("ticketRepository");

        if (ticketRepository == null) {
            logger.error("TicketRepository not found in ServletContext");
            throw new ServletException("Application not properly initialized");
        }

        logger.info("TicketServlet initialized with {} tickets", ticketRepository.size());
    }

    // --- GET ---
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType(CONTENT_TYPE_JSON);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetWithQueries(req, resp);
            } else {
                String identity = decodePathParam(pathInfo.substring(1));
                handleGetByIdentity(identity, resp);
            }
        } catch (DataSerializationException e) {
            logger.error("Serialization error in doGet", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // --- POST ---
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE_JSON);

        try {
            String requestBody = getRequestBody(req);
            Ticket ticket = ticketSerializer.fromString(requestBody, Ticket.class);

            ticketRepository.add(ticket);

            String identity = ticketRepository.getIdentity(ticket);
            logger.info("Ticket created: {}", identity);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            // ВИПРАВЛЕНО: Використовуємо ticketSerializer
            resp.getWriter().write(ticketSerializer.toString(ticket));

        } catch (AlreadyExistsException e) {
            logger.warn("Ticket already exists: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (InvalidDataException e) {
            logger.warn("Invalid ticket data: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (DataSerializationException e) {
            logger.error("Serialization error in doPost", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON: " + e.getMessage());
        }
    }

    // --- PUT ---
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType(CONTENT_TYPE_JSON);

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Ticket identity is required");
            return;
        }

        String identity = decodePathParam(pathInfo.substring(1));

        try {
            String requestBody = getRequestBody(req);
            Ticket updatedTicket = ticketSerializer.fromString(requestBody, Ticket.class);

            boolean updated = ticketRepository.update(updatedTicket);

            if (!updated) {
                logger.warn("Ticket not found: {}", identity);
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Ticket not found: " + identity);
                return;
            }

            logger.info("Ticket updated: {}", identity);
            resp.setStatus(HttpServletResponse.SC_OK);
            // ВИПРАВЛЕНО: Використовуємо ticketSerializer
            resp.getWriter().write(ticketSerializer.toString(updatedTicket));

        } catch (DataSerializationException e) {
            logger.error("Serialization error in doPut", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON: " + e.getMessage());
        }
    }

    // --- DELETE ---
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Ticket identity is required");
            return;
        }

        String identity = decodePathParam(pathInfo.substring(1));
        boolean removed = ticketRepository.removeByIdentity(identity);

        if (!removed) {
            logger.warn("Ticket not found for deletion: {}", identity);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Ticket not found: " + identity);
            return;
        }

        logger.info("Ticket deleted: {}", identity);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }


    private void handleGetWithQueries(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, DataSerializationException {

        String priceParam = req.getParameter("price");
        String statusParam = req.getParameter("status");
        String sortParam = req.getParameter("sort");
        String groupParam = req.getParameter("group");
        String countParam = req.getParameter("count");

        Object result;
        String logMessage;

        try {
            if (priceParam != null && !priceParam.isBlank()) {
                double price = Double.parseDouble(priceParam);
                result = ticketRepository.findByPrice(price);
                logMessage = String.format("Filter by price '%.2f': %d tickets", price, ((List<?>) result).size());
            } else if (statusParam != null && !statusParam.isBlank()) {
                result = ticketRepository.findByStatus(statusParam);
                logMessage = String.format("Filter by status '%s': %d tickets", statusParam, ((List<?>) result).size());
            } else if (sortParam != null && !sortParam.isBlank()) {
                result = handleSorting(sortParam);
                logMessage = String.format("Sorted all tickets by %s", sortParam);
            } else if (groupParam != null && !groupParam.isBlank()) {
                result = handleGrouping(groupParam);
                logMessage = String.format("Grouped all tickets by %s", groupParam);
            } else if (countParam != null && countParam.equals("status")) {
                result = ticketRepository.countByStatus();
                logMessage = "Counted tickets by status";
            } else {
                result = ticketRepository.getAll();
                logMessage = String.format("Retrieved all %d tickets", ((List<?>) result).size());
            }

            logger.info(logMessage);
            resp.setStatus(HttpServletResponse.SC_OK);

            if (result instanceof List) {
                // Використовуємо ticketSerializer
                resp.getWriter().write(ticketSerializer.listToString((List<Ticket>) result));
            } else if (result instanceof Map || result instanceof Long) {
                // Використовуємо generalSerializer
                resp.getWriter().write(generalSerializer.toString(result));
            } else if (result instanceof Ticket) {
                // Використовуємо ticketSerializer
                resp.getWriter().write(ticketSerializer.toString((Ticket) result));
            } else {
                // Fallback
                resp.getWriter().write(generalSerializer.toString(result));
            }
            // ------------------------------------

        } catch (NumberFormatException e) {
            logger.error("Invalid price parameter format: {}", priceParam);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid price format. Must be a decimal number.");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid sort/group parameter: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private List<Ticket> handleSorting(String sortParam) {
        return switch (sortParam.toLowerCase()) {
            case "seat" -> ticketRepository.sortBySeatNumber();
            case "movie" -> ticketRepository.sortByMovie();
            default -> throw new IllegalArgumentException("Unknown sort parameter: " + sortParam);
        };
    }

    private Object handleGrouping(String groupParam) {
        return switch (groupParam.toLowerCase()) {
            case "genre" -> ticketRepository.groupByGenre();
            case "price" -> ticketRepository.groupByPrice();
            default -> throw new IllegalArgumentException("Unknown group parameter: " + groupParam);
        };
    }

    private void handleGetByIdentity(String identity, HttpServletResponse resp)
            throws IOException, DataSerializationException {
        Optional<Ticket> ticketOptional = ticketRepository.findByIdentity(identity);

        if (ticketOptional.isPresent()) {
            logger.info("Found ticket: {}", identity);
            resp.setStatus(HttpServletResponse.SC_OK);

            Ticket ticket = ticketOptional.get();
            // ВИПРАВЛЕНО: Використовуємо ticketSerializer
            resp.getWriter().write(ticketSerializer.toString(ticket));

        } else {
            logger.warn("Ticket not found: {}", identity);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Ticket not found: " + identity);
        }
    }

    /**
     * Decode URL-encoded path parameter
     */
    private String decodePathParam(String param) {
        try {
            return java.net.URLDecoder.decode(param, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.warn("Failed to decode path param: {}", param);
            return param;
        }
    }

    @Override
    public void destroy() {
        logger.info("=== TicketServlet destroy() ===");
        logger.info("Total requests processed: {}", getRequestCount());
        logger.info("Final ticket count: {}",
                ticketRepository != null ? ticketRepository.size() : 0);
    }
}