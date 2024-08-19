package ir.tic.clouddc.event;

import jakarta.persistence.Entity;
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
}
