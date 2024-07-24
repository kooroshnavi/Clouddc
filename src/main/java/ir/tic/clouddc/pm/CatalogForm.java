package ir.tic.clouddc.pm;

import lombok.Data;

@Data
public class CatalogForm {

    private int pmInterfaceCatalogId;

    private Long locationId;

    private Long deviceId;

    private String nextDue;

    private int defaultPersonId;

    private boolean enabled;

    private int pmInterfaceId;
}
