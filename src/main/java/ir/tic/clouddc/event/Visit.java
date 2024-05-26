package ir.tic.clouddc.event;

import jakarta.persistence.Transient;

public class Visit extends Event {

    @Transient
    private final String EVENT_TYPE = "بازدید";

    public String getEVENT_TYPE() {
        return EVENT_TYPE;
    }
}
