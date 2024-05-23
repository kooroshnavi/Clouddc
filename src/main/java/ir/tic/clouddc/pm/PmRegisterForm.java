package ir.tic.clouddc.pm;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
public class PmRegisterForm {
    @NotBlank(message = "عنوان نباید خالی باشد")
    private String name;

    @NotBlank(message = "توضیحات نباید خالی باشد")
    private String description;

    private int period;

    private int personId;

    private List<Integer> salonIdList;

    private int typeId;

    private String persianFirstDueDate;

    private MultipartFile file;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public List<Integer> getSalonIdList() {
        return salonIdList;
    }

    public String getPersianFirstDueDate() {
        return persianFirstDueDate;
    }

    public void setPersianFirstDueDate(String persianFirstDueDate) {
        this.persianFirstDueDate = persianFirstDueDate;
    }

    public void setSalonIdList(List<Integer> salonIdList) {
        this.salonIdList = salonIdList;
    }
}
