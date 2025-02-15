package ir.tic.clouddc.rpc.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Response {

    private String status; // OK -- Error

    private String title;

    private String localDateTime;

    List<? extends Result> resultList;
}
