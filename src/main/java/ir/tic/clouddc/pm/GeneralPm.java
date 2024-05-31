package ir.tic.clouddc.pm;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class GeneralPm extends Pm { /// Text-based Pm. No specific field

}
