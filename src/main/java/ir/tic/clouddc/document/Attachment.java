package ir.tic.clouddc.document;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Document")
@NoArgsConstructor
@Getter
@Setter
public final class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AttachmentID")
    private Long id;

    @Lob
    @Column(name = "Document")
    private byte[] document;
}



