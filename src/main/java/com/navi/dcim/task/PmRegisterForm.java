package com.navi.dcim.task;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PmRegisterForm {
    private String name;

    private String description;

    private String persianDue;

    private int period;

    private int personId;

    private int centerId;

    public PmRegisterForm(String name, String persianDue, int period, int personId, int centerId) {
        this.name = name;
        this.persianDue = persianDue;
        this.period = period;
        this.personId = personId;
        this.centerId = centerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPersianDue(String persianDue) {
        this.persianDue = persianDue;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
    }

    public String getName() {
        return name;
    }

    public String getPersianDue() {
        return persianDue;
    }

    public int getPeriod() {
        return period;
    }

    public int getPersonId() {
        return personId;
    }

    public int getCenterId() {
        return centerId;
    }

    @Override
    public String toString() {
        return "pmRegisterForm{" +
                "name='" + name + '\'' +
                ", persianDue='" + persianDue + '\'' +
                ", period=" + period +
                ", personId=" + personId +
                ", centerId=" + centerId +
                '}';
    }
}
