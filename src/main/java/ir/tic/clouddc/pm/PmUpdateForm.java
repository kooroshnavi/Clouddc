package ir.tic.clouddc.pm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
public class PmUpdateForm {

    private int pmId;

    @NotBlank(message = "Description is obligatory")
    @Size(min = 5, max = 1000)
    private String description;

    @NotBlank
    private int actionType;


    private float temperatureValue;

    private MultipartFile file;

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

    public int getPmId() {
        return pmId;
    }

    public void setPmId(int pmId) {
        this.pmId = pmId;
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

    @Override
    public String toString() {
        return "AssignForm{" +
                "pmId=" + pmId +
                ", description='" + description + '\'' +
                ", actionType=" + actionType +
                '}';
    }
}
