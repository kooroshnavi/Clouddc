package ir.tic.clouddc.document;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Document")
@NoArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Lob
    private byte[] document;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getDocument() {
        return document;
    }

    public void setDocument(byte[] document) {
        this.document = document;
    }
}



