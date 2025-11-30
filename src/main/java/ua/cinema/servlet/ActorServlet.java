package ua.cinema.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cinema.exception.AlreadyExistsException;
import ua.cinema.exception.DataSerializationException;
import ua.cinema.exception.InvalidDataException;
import ua.cinema.model.Actor;
import ua.cinema.repository.ActorRepository;
import ua.cinema.serializer.JsonDataSerializer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * REST API Servlet for Actor CRUD operations
 *
 * Endpoints:
 * - GET    /actors                -> get all actors (with optional filters: name)
 * - GET    /actors?movie=X        -> filter by movie title
 * - GET    /actors/{identity}     -> get actor by identity (e.g., name or ID)
 * - POST   /actors                -> create new actor
 * - PUT    /actors/{identity}     -> update actor
 * - DELETE /actors/{identity}     -> delete actor
 */
@WebServlet(name = "ActorServlet", urlPatterns = {"/actors", "/actors/*"})
public class ActorServlet extends BaseServlet {
    private JsonDataSerializer<Actor> serializer;

    private ActorRepository actorRepository;

    @Override
    public void init() throws ServletException {
        logger.info("=== ActorServlet init() ===");

        serializer = new JsonDataSerializer<>();

        actorRepository = (ActorRepository) getServletContext()
                .getAttribute("actorRepository");

        if (actorRepository == null) {
            logger.error("ActorRepository not found in ServletContext");
            throw new ServletException("Application not properly initialized");
        }

        logger.info("ActorServlet initialized with {} actors", actorRepository.size());
    }

    // --- GET ---
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType(CONTENT_TYPE_JSON);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetWithFilters(req, resp);
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
            Actor actor = serializer.fromString(requestBody, Actor.class);

            actorRepository.add(actor);
            logger.info("Actor created: {}", actor.getIdentity());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(serializer.toString(actor));

        } catch (AlreadyExistsException e) {
            logger.warn("Actor already exists: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (InvalidDataException e) {
            logger.warn("Invalid actor data: {}", e.getMessage());
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
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Actor identity is required");
            return;
        }

        String identity = decodePathParam(pathInfo.substring(1));

        try {
            String requestBody = getRequestBody(req);
            Actor updatedActor = serializer.fromString(requestBody, Actor.class);
            boolean updated = actorRepository.update(updatedActor);

            if (!updated) {
                logger.warn("Actor not found: {}", identity);
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Actor not found: " + identity);
                return;
            }

            logger.info("Actor updated: {}", identity);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.toString(updatedActor));

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
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Actor identity is required");
            return;
        }

        String identity = decodePathParam(pathInfo.substring(1));
        boolean removed = actorRepository.removeByIdentity(identity);

        if (!removed) {
            logger.warn("Actor not found for deletion: {}", identity);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Actor not found: " + identity);
            return;
        }

        logger.info("Actor deleted: {}", identity);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void handleGetWithFilters(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, DataSerializationException {

        String name = req.getParameter("name");
        String yearParam = req.getParameter("year");

        List<Actor> actors;

        try {
            if (name != null && !name.isBlank()) {
                actors = actorRepository.findByFirstName(name);
                logger.info("Filter by name '{}': {} actors", name, actors.size());

            } else if (yearParam != null && !yearParam.isBlank()) {
                int minYear = Integer.parseInt(yearParam);

                actors = actorRepository.findByMinBirthYear(minYear);

                logger.info("Filter by min birth year '{}': {} actors", minYear, actors.size());

            } else {
                actors = actorRepository.getAll();
                logger.info("Retrieved all {} actors", actors.size());
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.listToString(actors));

        } catch (NumberFormatException e) {

            logger.error("Invalid year parameter format: {}", yearParam);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid year format. Must be an integer.");
        }
    }


    private void handleGetByIdentity(String identity, HttpServletResponse resp)
            throws IOException, DataSerializationException {
        Optional<Actor> actor = actorRepository.findByIdentity(identity);

        if (actor.isPresent()) {
            logger.info("Found actor: {}", identity);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(serializer.toString(actor.get()));
        } else {
            logger.warn("Actor not found: {}", identity);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Actor not found: " + identity);
        }
    }

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
        logger.info("=== ActorServlet destroy() ===");
        logger.info("Total requests processed: {}", getRequestCount());
        logger.info("Final actor count: {}",
                actorRepository != null ? actorRepository.size() : 0);
    }
}