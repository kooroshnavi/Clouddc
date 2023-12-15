package com.navi.dcim.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EventForm {

    @NotBlank
    private int eventType;
    @NotBlank
    private String description;
    @NotNull
    private boolean active;
    @NotBlank
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
