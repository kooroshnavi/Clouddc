package com.navi.dcim.task;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ModifyTaskForm {
    private int personId;
    private int centerId;

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
    }
}
