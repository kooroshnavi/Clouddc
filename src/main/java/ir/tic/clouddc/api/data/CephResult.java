package ir.tic.clouddc.api.data;

import ir.tic.clouddc.api.response.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CephResult extends Result {

    private int id;

    private String title;

    private String value;

    private String unit;
}
