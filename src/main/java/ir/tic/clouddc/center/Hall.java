package ir.tic.clouddc.center;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(schema = "center")
@NoArgsConstructor
public final class Hall extends Location {
    @OneToMany(mappedBy = "hall")
    private List<Rack> rackList;

    public List<Rack> getRackList() {
        return rackList;
    }

    public void setRackList(List<Rack> rackList) {
        this.rackList = rackList;
    }
}
