package ir.tic.clouddc.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class Response {

    private String title;

    private String localDateTime;

    List<? extends Result> resultList;
}
