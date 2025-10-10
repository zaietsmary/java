package ua.cinema.repository;

@FunctionalInterface
interface IdentityExtractor<T> {
    String extractIdentity(T object);
}