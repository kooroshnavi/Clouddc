package ir.tic.clouddc.pm;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class GeneralPm extends Pm { /// Text-based Pm. No specific field

    @OneToMany(mappedBy = "pm", cascade = CascadeType.ALL)
    private List<Task> taskList;



}
