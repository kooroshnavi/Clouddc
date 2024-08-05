package ir.tic.clouddc.event;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class EventForm {   //// DTO

    private Integer eventCategoryId;   /// 2. Location CheckList 3.Device Utilizer 4. Device Movement  5. Device Checklist

    private Long locationChecklist_LocationId;

    private Integer deviceUtilizer_newUtilizer;

    private List<Long> deviceUtilizer_deviceIdList;

    private List<Long> DeviceMovement_deviceIdList;

    private Long DeviceMovement_sourceLocId;

    private Long DeviceMovement_destLocId;

    private String description;

    private String date;

    private MultipartFile multipartFile;
}
