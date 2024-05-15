package ir.tic.clouddc.document;


import ir.tic.clouddc.log.LogHistory;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(schema = "Document")
@NoArgsConstructor
public class MetaData {

    @Id
    private long id;

    @Column
    @Nationalized
    private String name;

    @Column
    private String type;

    @Column
    private float size;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "persistence_id")
    private LogHistory logHistory;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Attachment attachment;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public LogHistory getLogHistory() {
        return logHistory;
    }

    public void setLogHistory(LogHistory logHistory) {
        this.logHistory = logHistory;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

}
