package ua.cinema.repository;

@FunctionalInterface
public interface IdentityExtractor<T> {
    String extractIdentity(T object);
}