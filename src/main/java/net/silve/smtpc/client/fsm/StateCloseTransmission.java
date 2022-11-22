package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;

import java.util.Objects;

public class StateCloseTransmission implements State {

    public static final State CLOSING_TRANSMISSION_STATE = new StateCloseTransmission();

    @Override
    public SendStatus checkSentStatus(FsmEvent event) {
        SmtpResponse response = event.getResponse();
        if (Objects.isNull(response)) {
            return null;
        }
        return new SendStatus(SendStatusCode.NOT_SENT, response.code(), response.details());
    }

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
        return null;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.CLOSE_TRANSMISSION;
    }
}
