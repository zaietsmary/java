package ua.cinema.repository;

import ua.cinema.model.Actor;

public class ActorIdentity extends Actor implements Identity {

    public ActorIdentity(String firstName, String lastName, int birthYear) {
        super(firstName, lastName, birthYear);
    }

    @Override
    public String getIdentity() {
        return getFirstName() + " " + getLastName() + " (" + getBirthYear() + ")";
    }
}

class ActorRepo extends GenericRepositoryForInterface<ActorIdentity> {

    public ActorRepo() {
        super("Actor identity");
    }
}
