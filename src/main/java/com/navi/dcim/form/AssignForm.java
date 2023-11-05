package com.navi.dcim.form;

import lombok.NoArgsConstructor;
import org.hibernate.boot.archive.scan.internal.ScanResultImpl;

@NoArgsConstructor
public class AssignForm {
    private String description;
    private int type;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
