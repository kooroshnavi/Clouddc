package ir.tic.clouddc.log;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Log")
@NoArgsConstructor
public class Persistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;


    @OneToMany(mappedBy = "persistence", cascade = {CascadeType.ALL})
    private List<LogHistory> logHistoryList;

    public long getId() {
        return id;
    }

    public List<LogHistory> getLogHistoryList() {
        return logHistoryList;
    }

    public void setLogHistoryList(List<LogHistory> logHistoryList) {
        this.logHistoryList = logHistoryList;
    }

}