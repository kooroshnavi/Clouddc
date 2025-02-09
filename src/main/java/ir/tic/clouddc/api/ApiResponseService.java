package ir.tic.clouddc.api;

import java.util.List;

public interface ApiResponseService {

    Response createResponse(String title, List<? extends Result> resultList);
}
