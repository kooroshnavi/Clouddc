package ir.tic.clouddc.cloud;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Cloud")
@NoArgsConstructor
@Getter
@Setter
public class CephUtilizer extends CloudUtilizer {

    @Column(name = "Usage", nullable = false)
    private float usage;

    @Column(name = "Unit", nullable = false)
    private String unit;
}
