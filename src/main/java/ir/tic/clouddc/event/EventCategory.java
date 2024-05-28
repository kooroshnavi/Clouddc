package ir.tic.clouddc.event;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(schema = "Center")
public final class EventCategory {
    public EventCategory(int id) {
        this.id = id;
    }

    private int id;

    private String name;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
