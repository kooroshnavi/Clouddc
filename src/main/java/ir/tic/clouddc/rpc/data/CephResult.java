package ir.tic.clouddc.rpc.data;

import ir.tic.clouddc.rpc.response.Result;
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
