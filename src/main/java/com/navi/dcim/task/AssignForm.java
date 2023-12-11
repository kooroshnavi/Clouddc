package com.navi.dcim.task;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AssignForm {

    private int id;
    private String description;
    private int actionType;

    public AssignForm(String description, int actionType) {
        this.description = description;
        this.actionType = actionType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
                "id=" + id +
                ", description='" + description + '\'' +
                ", actionType=" + actionType +
                '}';
    }
}
