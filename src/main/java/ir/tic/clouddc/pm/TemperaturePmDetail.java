package ir.tic.clouddc.pm;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class TemperaturePmDetail extends PmDetail {

    @ManyToOne
    @JoinColumn(name = "pm_id")
    private Pm temperaturePm;

    @Column
    private float value;    /// Specific for TemperaturePmDetail

    public Pm getTemperaturePm() {
        return temperaturePm;
    }

    public void setTemperaturePm(Pm temperaturePm) {
        this.temperaturePm = temperaturePm;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }


}
