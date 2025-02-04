package ir.tic.clouddc.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CephApiData {

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
