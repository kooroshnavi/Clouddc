package ir.tic.clouddc.resource;

import lombok.Data;

@Data
public class DeviceModuleUpdateForm {

    private long deviceId;

    private int moduleInventoryId;

    private Integer updatedValue = null;
}
