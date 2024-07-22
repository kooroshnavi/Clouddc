package ir.tic.clouddc.pm;

import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@NoArgsConstructor
public class PmUpdateForm {

    private Pm pm;

    private String description;

    private String ownerUsername;

    private int actionType;

    private float temperatureValue;

    private MultipartFile file;

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public float getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(float temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    public PmUpdateForm(String description, int actionType) {
        this.description = description;
        this.actionType = actionType;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public int getActionType() {
        return actionType;
    }

    public Pm getPm() {
        return pm;
    }

    public void setPm(Pm pm) {
        this.pm = pm;
    }
}
