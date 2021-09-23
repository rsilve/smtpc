package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;

public interface FsmActionListener {
    void onAction(SmtpCommandAction action, SmtpResponse response);
}
