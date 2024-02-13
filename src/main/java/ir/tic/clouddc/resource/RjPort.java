package ir.tic.clouddc.resource;


import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class RjPort extends Port {

    private static final String TYPE = "Device - RJ-45";





}
