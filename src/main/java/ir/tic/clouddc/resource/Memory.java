package ir.tic.clouddc.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class Memory extends Module {

    @Column
    private String speed; // 2666-  MHz

    @Column
    private int capacity;     // 64 - GB

}
