package net.silve.smtpc.client.fsm;

public class ConstantStates {

    public static final State QUIT_STATE = new StateQuit();
    public static final State CLOSING_TRANSMISSION_STATE = new StateCloseTransmission();
    public static final State INIT_STATE = new StateInit();
    public static final State EXTENDED_GREETING_STATE = new StateExtendedGreeting();
    public static final State GREETING_STATE = new StateGreeting();
    public static final State STARTTLS_STATE = new StateStartTls();
    public static final State TLS_HANDSHAKE_STATE = new TlsHandshakeState();
    public static final State PIPELINING_MAIL_STATE = new StatePipeliningMail();
    public static final State PIPELINING_RCPT_STATE = new StatePipeliningRcpt();
    public static final State PIPELINING_DATA_STATE = new StatePipeliningData();
    public static final State PIPELINING_MAIL_RESPONSE_STATE = new StatePipeliningMailResponse();
    public static final State PIPELINING_RCPT_RESPONSE_STATE = new StatePipeliningRcptResponse();
    public static final State PIPELINING_DATA_RESPONSE_STATE = new StatePipeliningDataResponse();
    public static final State MAIL_STATE = new StateMail();
    public static final State RCPT_STATE = new StateRcpt();
    public static final State DATA_STATE = new StateData();
    public static final State CONTENT_STATE = new StateContent();
    public static final State RSET_STATE = new StateRset();


    private ConstantStates() {}
}
