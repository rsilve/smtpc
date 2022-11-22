package net.silve.smtpc.model;


import java.util.Collections;
import java.util.List;

public class SendStatus {

    private final SendStatusCode code;
    private final int responseCode;
    private final List<CharSequence> details;

    public SendStatus(SendStatusCode code, int responseCode, List<CharSequence> details) {
        this.code = code;
        this.responseCode = responseCode;
        this.details = details;
    }

    public SendStatusCode getCode() {
        return code;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public List<CharSequence> getDetails() {
        return Collections.unmodifiableList(details);
    }

    @Override
    public String toString() {
        return "SendStatus{" +
                "code=" + code +
                ", responseCode=" + responseCode +
                ", details=" + details +
                '}';
    }
}
