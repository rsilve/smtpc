package net.silve.smtpc.client.fsm;

import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.StateCloseTransmission.CLOSING_TRANSMISSION_STATE;
import static net.silve.smtpc.client.fsm.StateQuit.QUIT_STATE;


public class InvalidStateException extends Exception {

    public static final InvalidStateException INVALID_STATE_EXCEPTION_QUIT = new InvalidStateException(QUIT_STATE);
    public static final InvalidStateException INVALID_STATE_EXCEPTION_CLOSING_TRANSMISSION = new InvalidStateException(CLOSING_TRANSMISSION_STATE);
    private final transient State state;

    public InvalidStateException(@NotNull State state) {
        super();
        this.state = state;
    }

    public State getState() {
        return this.state;
    }

}