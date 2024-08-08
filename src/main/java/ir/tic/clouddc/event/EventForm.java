package ir.tic.clouddc.event;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class EventForm {   //// DTO

    private Integer eventCategoryId;   /// 2. Location CheckList 3.Utilizer   4. Device Movement     5. Device Checklist

    private Integer utilizer_oldUtilizerId;

    private Integer utilizer_newUtilizerId;

    private Long utilizer_locationId;

    private Long utilizer_deviceId;

    private List<Long> deviceIdList;

    private Long DeviceMovement_sourceLocId;

    private Long DeviceMovement_destLocId;

    private String description;

    private String date;

    private MultipartFile multipartFile;
}
