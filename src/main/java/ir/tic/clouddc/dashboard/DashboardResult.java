package ir.tic.clouddc.dashboard;

import ir.tic.clouddc.api.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardResult extends Result {

    private int id;

    private String title;

    private String value;

    private String unit;
}
