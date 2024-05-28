package ir.tic.clouddc.log;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Log")
@NoArgsConstructor
public final class LogMessage {
    private int id;

    private String type;

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
