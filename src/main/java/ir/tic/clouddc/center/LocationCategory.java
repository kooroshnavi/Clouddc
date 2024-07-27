package ir.tic.clouddc.center;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
@Data
public final class LocationCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;  // 1 - 2 - 3

    @Column
    private String category; /// Hall - Rack - Room

    @Column
    private int categoryId;

    @OneToMany(mappedBy = "locationCategory")
    private List<Location> locationList;
}

