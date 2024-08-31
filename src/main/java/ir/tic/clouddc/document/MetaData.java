package ir.tic.clouddc.document;


import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(schema = "Document")
@NoArgsConstructor
@Getter
@Setter
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

    @Column(name = "RemoveDate")
    private LocalDate removeDate;

    @Transient
    private String persianUploadDate;

    @Transient
    private String persianRemoveDate;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @MapsId
    @JoinColumn(name = "DocumentID")
    private Attachment attachment;

}

