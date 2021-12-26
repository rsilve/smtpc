package net.silve.smtpc.client.fsm;

import org.jetbrains.annotations.NotNull;


public class InvalidStateException extends Exception {

    private final transient State state;

    public InvalidStateException(@NotNull State state) {
        super();
        this.state = state;
    }

    @NotNull
    public State getState() {
        return this.state;
    }

}