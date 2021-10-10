package net.silve.smtpc.fsm;

public enum SmtpCommandAction {

    HELO, EHLO,
    STARTTLS, TLS_HANDSHAKE,
    MAIL, RCPT,
    DATA,
    DATA_CONTENT,
    RSET,
    QUIT,
    CLOSE_TRANSMISSION

}
