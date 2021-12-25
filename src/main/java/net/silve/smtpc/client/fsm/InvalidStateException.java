package net.silve.smtpc.client.fsm;

public class InvalidStateException extends Exception {

    private final State state;

    public InvalidStateException(State state) {
        super();
        this.state = state;
    }

    public State getState() {
        return this.state;
    }

}