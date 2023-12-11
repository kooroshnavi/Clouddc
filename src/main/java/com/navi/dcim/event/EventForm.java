package com.navi.dcim.event;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EventForm {

    private int eventType;
    private String description;
    private boolean active;
    private int centerId;

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "EventForm{" +
                "eventType=" + eventType +
                ", description='" + description + '\'' +
                ", active=" + active +
                '}';
    }
}
