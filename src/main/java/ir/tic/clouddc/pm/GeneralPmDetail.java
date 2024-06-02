package ir.tic.clouddc.pm;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class GeneralPmDetail extends PmDetail {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pm_id")
    private Pm generalPm;

    public Pm getGeneralPm() {
        return generalPm;
    }

    public void setGeneralPm(Pm generalPm) {
        this.generalPm = generalPm;
    }
}

