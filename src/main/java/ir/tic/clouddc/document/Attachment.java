package ir.tic.clouddc.document;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Document")
@NoArgsConstructor
@Data
public final class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AttachmentID")
    private Long id;

    @Lob
    @Column(name = "Document")
    private byte[] document;
}



