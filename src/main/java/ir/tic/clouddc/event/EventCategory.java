package ir.tic.clouddc.event;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class EventCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private short id;

    @Column
    @Nationalized
    private String name;

    @Column
    private short target;   //  1.Center - 2.Location  - 3. Device

    public short getTarget() {
        return target;
    }

    public void setTarget(short target) {
        this.target = target;
    }

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
