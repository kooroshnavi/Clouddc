package ir.tic.clouddc.log;


import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Log")
@NoArgsConstructor
public final class LogMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String type;

    @Column
    private String message;

    public LogMessage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
