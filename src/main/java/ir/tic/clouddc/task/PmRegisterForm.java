package ir.tic.clouddc.task;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PmRegisterForm {
    @NotBlank(message = "عنوان نباید خالی باشد")
    private String name;

    @NotBlank(message = "توضیحات نباید خالی باشد")
    private String description;

    @NotNull
    private int period;

    @NotNull
    private int personId;

    @NotNull
    private int centerId;

    public PmRegisterForm(String name, int period, int personId, int centerId) {
        this.name = name;
        this.period = period;
        this.personId = personId;
        this.centerId = centerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
    }

    public String getName() {
        return name;
    }

    public int getPeriod() {
        return period;
    }

    public int getPersonId() {
        return personId;
    }

    public int getCenterId() {
        return centerId;
    }

    @Override
    public String toString() {
        return "pmRegisterForm{" +
                "name='" + name + '\'' +
                ", period=" + period +
                ", personId=" + personId +
                ", centerId=" + centerId +
                '}';
    }
}
