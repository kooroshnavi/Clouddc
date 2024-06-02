package ir.tic.clouddc.pm;

import com.github.mfathi91.time.PersianDate;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
public class pmInterfaceRegisterForm {

    private int id;

    @NotBlank(message = "عنوان نباید خالی باشد")
    private String name;

    @NotBlank(message = "توضیحات نباید خالی باشد")
    private String description;

    private int period;

    private boolean enabled;

    private List<Integer> locationIdList;

    private int categoryId;

    private PersianDate persianFirstDueDate;

    private MultipartFile file;

    public List<Integer> getLocationIdList() {
        return locationIdList;
    }

    public void setLocationIdList(List<Integer> locationIdList) {
        this.locationIdList = locationIdList;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PersianDate getPersianFirstDueDate() {
        return persianFirstDueDate;
    }

    public void setPersianFirstDueDate(PersianDate persianFirstDueDate) {
        this.persianFirstDueDate = persianFirstDueDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }


}
