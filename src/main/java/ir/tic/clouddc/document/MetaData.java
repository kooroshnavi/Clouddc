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

    @Column(name = "FileName")
    private String name;

    @Column(name = "FileType")
    private String type;

    @Column(name = "SizeOnDisk")
    private float size;

    @Column(name = "Enabled")
    private boolean enabled;

    @Column(name = "UploadDate")
    private LocalDate uploadDate;

    @Column(name = "DisableDate")
    private LocalDate disableDate;

    @Transient
    private String persianUploadDate;

    @Transient
    private String persianDisableDate;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @MapsId
    @JoinColumn(name = "DocumentID")
    private Attachment attachment;

}
