package ir.tic.clouddc.api.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.tic.clouddc.api.response.Result;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RemoteResult extends Result {

    private String status;

    private Data data;

    @lombok.Data
    public static class Data {

        @JsonIgnore
        private String resultType;

        private List<Result> result;
    }

    @lombok.Data
    public static class Result {

        @JsonIgnore
        private Map<String, String> metric;

        private List<Object> value;
    }
}
