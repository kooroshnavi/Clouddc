package ir.tic.clouddc.log;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedBy
    protected String createdBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdDate;

    @LastModifiedBy
    protected String lastModifiedBy;

    @LastModifiedDate
    protected LocalDateTime lastModifiedDate;
}
