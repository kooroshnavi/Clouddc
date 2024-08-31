package ir.tic.clouddc.center;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
@Getter
@Setter
public final class LocationCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LocationCategoryID")
    private Integer locationCategoryID;  // 1 - 2 - 3

    @Column(name = "Category", columnDefinition = "nvarchar(50)")
    private String category; /// Hall - Rack - Room

    @Column(name = "CategoryId")
    private int categoryId;

    @OneToMany(mappedBy = "locationCategory")
    private List<Location> locationList;
}

