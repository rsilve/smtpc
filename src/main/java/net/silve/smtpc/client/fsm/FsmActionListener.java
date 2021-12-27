package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

public interface FsmActionListener {
    void onAction(@NotNull SmtpCommandAction action, SmtpResponse response);
    void onError(InvalidStateException exception);
}
