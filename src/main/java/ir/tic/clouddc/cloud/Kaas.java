package ir.tic.clouddc.cloud;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Cloud")
@NoArgsConstructor
@Getter
@Setter
public class Kaas extends CloudProvider {

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<KaasUtilizer> kaasUtilizerList;

}
