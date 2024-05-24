package ir.tic.clouddc.pm;

import com.github.mfathi91.time.PersianDate;
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

    private List<Integer> salonIdList;

    private List<Integer> roomIdList;

    private int typeId;

    private PersianDate persianFirstDueDate;

    private MultipartFile file;

    public List<Integer> getRoomIdList() {
        return roomIdList;
    }

    public void setRoomIdList(List<Integer> roomIdList) {
        this.roomIdList = roomIdList;
    }

    public PersianDate getPersianFirstDueDate() {
        return persianFirstDueDate;
    }

    public void setPersianFirstDueDate(PersianDate persianFirstDueDate) {
        this.persianFirstDueDate = persianFirstDueDate;
    }

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

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public List<Integer> getSalonIdList() {
        return salonIdList;
    }


    public void setSalonIdList(List<Integer> salonIdList) {
        this.salonIdList = salonIdList;
    }
}
