package com.navi.dcim.form;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AssignForm {
    private String description;
    private int actionType;

    public AssignForm(String description, int actionType) {
        this.description = description;
        this.actionType = actionType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public int getActionType() {
        return actionType;
    }

    @Override
    public String toString() {
        return "AssignForm{" +
                "description='" + description + '\'' +
                ", actionType=" + actionType +
                '}';
    }
}
