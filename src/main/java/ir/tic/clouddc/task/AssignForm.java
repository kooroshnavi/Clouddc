package ir.tic.clouddc.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AssignForm {

    @NotNull
    private Long id;

    @NotBlank(message = "Description is obligatory")
    @Size(min = 5, max = 1000)
    private String description;

    @NotBlank
    private int actionType;

    public AssignForm(String description, int actionType) {
        this.description = description;
        this.actionType = actionType;
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
