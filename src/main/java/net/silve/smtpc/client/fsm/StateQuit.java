package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.StateCloseTransmission.CLOSING_TRANSMISSION_STATE;

public class StateQuit implements State {


    public static final State QUIT_STATE = new StateQuit();

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
        return CLOSING_TRANSMISSION_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.QUIT;
    }
}
