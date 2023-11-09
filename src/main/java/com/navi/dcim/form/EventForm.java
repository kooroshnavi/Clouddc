package com.navi.dcim.form;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EventForm {

    private int eventType;
    private int status;
    private String description;
    private boolean active;

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
                ", status=" + status +
                ", description='" + description + '\'' +
                ", active=" + active +
                '}';
    }
}
