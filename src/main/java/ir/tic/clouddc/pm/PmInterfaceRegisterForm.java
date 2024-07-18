package ir.tic.clouddc.pm;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
public class PmInterfaceRegisterForm {

    private PmInterface pmInterface;

    @NotBlank(message = "عنوان نباید خالی باشد")
    private String title;

    @NotBlank(message = "توضیحات نباید خالی باشد")
    private String description;

    private int period;

    private boolean enabled;

    private boolean statelessRecurring;

    private MultipartFile file;

    public boolean isStatelessRecurring() {
        return statelessRecurring;
    }

    public void setStatelessRecurring(boolean statelessRecurring) {
        this.statelessRecurring = statelessRecurring;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public PmInterface getPmInterface() {
        return pmInterface;
    }

    public void setPmInterface(PmInterface pmInterface) {
        this.pmInterface = pmInterface;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
