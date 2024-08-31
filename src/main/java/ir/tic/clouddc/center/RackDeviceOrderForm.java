package ir.tic.clouddc.center;

import lombok.Data;

import java.util.List;

@Data
public class RackDeviceOrderForm {

    private Long rackId;

    private List<String> orderList;
}
