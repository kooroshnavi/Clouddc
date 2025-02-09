package ir.tic.clouddc.api;

import ir.tic.clouddc.utils.UtilService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiResponseServiceImpl implements ApiResponseService {
    @Override
    public Response createResponse(String title, List<? extends Result> resultList) {

        return new Response(title
                , UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime())
                , resultList);
    }
}
