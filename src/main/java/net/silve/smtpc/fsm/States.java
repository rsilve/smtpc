package net.silve.smtpc.fsm;

public class States {

    public static final State INIT_STATE = new InitState();
    public static final State GREETING_STATE = new GreetingState();
    public static final State QUIT_AND_CLOSE_STATE = new QuitAndCloseState();
    public static final State CLOSING_TRANSMISSION_STATE = new CloseTransmissionState();

}
