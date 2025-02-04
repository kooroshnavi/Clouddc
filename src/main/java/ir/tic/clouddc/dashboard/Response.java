package ir.tic.clouddc.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {

    private int id;

    private String title;

    private String value;

    private String unit;
}
