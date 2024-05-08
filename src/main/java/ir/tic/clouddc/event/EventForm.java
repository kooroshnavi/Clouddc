package ir.tic.clouddc.event;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
public class EventForm {

    @NotBlank
    private int eventType;

    @NotBlank
    private String description;

    private boolean active;

    @NotBlank
    private int centerId;

    private MultipartFile attachment;


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

    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
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
