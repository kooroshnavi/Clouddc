package com.navi.dcim.form;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AssignForm {
    private String description;
    private int actionType;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }
}
