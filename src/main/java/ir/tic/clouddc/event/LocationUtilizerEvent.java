package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
@Getter
@Setter
public class LocationUtilizerEvent extends Event {

    @ManyToOne
    @JoinColumn(name = "OldUtilizerID")
    private Utilizer oldUtilizer;

    @ManyToOne
    @JoinColumn(name = "NewUtilizerID")
    private Utilizer newUtilizer;
}
