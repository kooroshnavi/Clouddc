package ir.tic.clouddc.center;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
@Getter
@Setter
public final class Hall extends Location {

    @OneToMany(mappedBy = "hall")
    private List<Rack> rackList;
}
