package ir.tic.clouddc.document;


import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(schema = "Document")
@NoArgsConstructor
@Data
public final class MetaData {

    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private String type;

    @Column
    private float size;

    @Column
    private boolean enabled;

    @Column
    private LocalDate uploadDate;

    @Column
    private LocalDate disableDate;

    @Transient
    private String persianUploadDate;

    @Transient
    private String persianDisableDate;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @MapsId
    @JoinColumn(name = "id")
    private Attachment attachment;

}
