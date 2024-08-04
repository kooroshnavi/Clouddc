package ir.tic.clouddc.pm;

import lombok.Data;

@Data
public class CatalogForm {

    private Long pmInterfaceCatalogId;

    private Long locationId;

    private Long deviceId;

    private String nextDue;

    private Integer defaultPersonId;

    private boolean enabled;

    private Integer pmInterfaceId;
}
