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
    @Column
    private Long id;

    @Lob
    private byte[] document;
}



