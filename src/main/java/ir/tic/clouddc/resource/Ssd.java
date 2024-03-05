package ir.tic.clouddc.resource;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class Ssd extends Storage {
    private static final String TYPE = "Storage-SSD";

    @Column
    private boolean nvme;

    @Column
    private boolean m2;

}
