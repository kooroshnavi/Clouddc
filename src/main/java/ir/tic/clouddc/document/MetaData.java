package ir.tic.clouddc.document;


import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(schema = "document")
@NoArgsConstructor
@Data
public class MetaData {

    @Id
    private Long id;

    @Column
    @Nationalized
    private String name;

    @Column
    private String type;

    @Column
    private float size;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @MapsId
    @JoinColumn(name = "id")
    private Attachment attachment;

}
