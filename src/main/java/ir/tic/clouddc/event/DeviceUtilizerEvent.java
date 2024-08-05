package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(schema = "Event")
@NoArgsConstructor
public final class DeviceUtilizerEvent extends Event {

    @ManyToOne
    @JoinColumn(name = "OldUtilizerID")
    private Utilizer oldUtilizer;

    @ManyToOne
    @JoinColumn(name = "NewUtilizerID")
    private Utilizer newUtilizer;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "DeviceID")
    private Device device;

}
