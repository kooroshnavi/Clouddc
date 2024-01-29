package ir.tic.clouddc.task;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
public class PmRegisterForm {
    @NotBlank(message = "عنوان نباید خالی باشد")
    private String name;

    @NotBlank(message = "توضیحات نباید خالی باشد")
    private String description;

    private int period;

    private int personId;

    private int salonId;

    private LocalDate dueDate;


    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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

    public void setSalonId(int salonId) {
        this.salonId = salonId;
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

    public int getSalonId() {
        return salonId;
    }

    @Override
    public String toString() {
        return "pmRegisterForm{" +
                "name='" + name + '\'' +
                ", period=" + period +
                ", personId=" + personId +
                ", salonId=" + salonId +
                '}';
    }
}
