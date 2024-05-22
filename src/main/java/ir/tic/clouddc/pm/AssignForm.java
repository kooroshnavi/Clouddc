package ir.tic.clouddc.pm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
public class AssignForm {

    private Long id;

    @NotBlank(message = "Description is obligatory")
    @Size(min = 5, max = 1000)
    private String description;

    @NotBlank
    private int actionType;

    private MultipartFile file;

    public AssignForm(String description, int actionType) {
        this.description = description;
        this.actionType = actionType;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
                "id=" + id +
                ", description='" + description + '\'' +
                ", actionType=" + actionType +
                '}';
    }
}
