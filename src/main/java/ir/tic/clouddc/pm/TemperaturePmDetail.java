package ir.tic.clouddc.pm;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "pm")
@NoArgsConstructor
public class TemperaturePmDetail extends PmDetail {

    @Column
    private float temperatureValue;    /// Specific for TemperaturePmDetail

    public float getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(float temperatureValue) {
        this.temperatureValue = temperatureValue;
    }


}
