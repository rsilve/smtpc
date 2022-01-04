package net.silve.smtpc.client.fsm;

public enum SmtpCommandAction {

    HELO, EHLO,
    STARTTLS, TLS_HANDSHAKE,
    PIPELINING,
    MAIL, RCPT,
    DATA,
    DATA_CONTENT,
    RSET,
    QUIT,
    CLOSE_TRANSMISSION

}
