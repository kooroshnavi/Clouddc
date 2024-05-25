package ir.tic.clouddc.center;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TemperatureForm {

    private String salon1Temp;

    public String getSalon1Temp() {
        return salon1Temp;
    }

    public void setSalon1Temp(String salon1Temp) {
        this.salon1Temp = salon1Temp;
    }
}
