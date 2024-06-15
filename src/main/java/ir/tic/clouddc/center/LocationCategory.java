package ir.tic.clouddc.center;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class LocationCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private short id;  // 1 - 2 - 3

    @Column
    @Nationalized
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
