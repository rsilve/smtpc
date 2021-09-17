package net.silve.smtpc.fse;

public enum SmtpCommandAction {

    QUIT_AND_CLOSE,
    HELO, EHLO,
    STARTTLS, TLS_HANDSHAKE,
    MAIL, RCPT,
    DATA,
    DATA_CONTENT,
    QUIT,
    CLOSE_TRANSMISSION
    ;

}
