package ir.tic.clouddc.center;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TemperatureForm {

    private String salon1Temp;

    private String salon2Temp;

    public TemperatureForm(String salon1Temp, String salon2Temp) {
        this.salon1Temp = salon1Temp;
        this.salon2Temp = salon2Temp;
    }

    public String getSalon1Temp() {
        return salon1Temp;
    }

    public void setSalon1Temp(String salon1Temp) {
        this.salon1Temp = salon1Temp;
    }

    public String getSalon2Temp() {
        return salon2Temp;
    }

    public void setSalon2Temp(String salon2Temp) {
        this.salon2Temp = salon2Temp;
    }
}
