package net.silve.smtpc.client.fsm;

import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.ConstantStates.QUIT_STATE;


public class InvalidStateException extends Exception {

    public static final InvalidStateException INVALID_STATE_EXCEPTION_QUIT = new InvalidStateException(QUIT_STATE);
    private final transient State state;

    public InvalidStateException(@NotNull State state) {
        super();
        this.state = state;
    }

    public State getState() {
        return this.state;
    }

}