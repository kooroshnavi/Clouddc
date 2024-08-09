package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
@Getter
@Setter
public final class DeviceUtilizerEvent extends Event {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OldUtilizerID")
    private Utilizer oldUtilizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NewUtilizerID")
    private Utilizer newUtilizer;
}
