package ir.tic.clouddc.resource;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeviceModuleUpdateForm {

    private long deviceId;

    private int moduleInventoryId;

    private Integer updatedValue = null;

    private List<Long> storageIdList = new ArrayList<>();

    private boolean storageUpdate;
}
