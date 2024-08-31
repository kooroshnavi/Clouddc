package ir.tic.clouddc.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
@Getter
@Setter
public class GeneralEvent extends Event {

    @Column(name = "GeneralCategoryID")
    private Integer generalCategoryId;

    @Transient
    private String category;
}
