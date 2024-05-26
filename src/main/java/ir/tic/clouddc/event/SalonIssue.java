package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Salon;
import jakarta.persistence.Transient;

public class SalonIssue extends Event{

    @Transient
    private final String EVENT_TYPE = "مشکل سالن";

    private Salon salon;

    public String getEVENT_TYPE() {
        return EVENT_TYPE;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }
}
