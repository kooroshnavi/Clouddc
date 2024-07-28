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
    @Column(name = "LocationCategoryID")
    private Integer locationCategoryID;  // 1 - 2 - 3

    @Column(name = "Category")
    private String category; /// Hall - Rack - Room

    @Column(name = "CategoryId")
    private int categoryId;

    @OneToMany(mappedBy = "locationCategory")
    private List<Location> locationList;
}

