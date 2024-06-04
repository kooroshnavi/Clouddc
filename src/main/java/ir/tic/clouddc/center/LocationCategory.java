package ir.tic.clouddc.center;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class LocationCategory {

    private short id;  // 1 - 2 - 3

    private String name; /// Salon - Rack - Room

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
