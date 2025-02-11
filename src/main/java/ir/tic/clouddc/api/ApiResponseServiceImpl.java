package ir.tic.clouddc.api;

import ir.tic.clouddc.utils.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApiResponseServiceImpl implements ApiResponseService {

    private final AuthTokenRepository authTokenRepository;

    @Autowired
    public ApiResponseServiceImpl(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    @Override
    public Response createResponse(String title, List<? extends Result> resultList) {

        return new Response(title
                , UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime())
                , resultList);
    }

    @Override
    public boolean isAuthTokenValid(String token) {
        return authTokenRepository.existsByTokenAndValidAndExpiryDateAfter(token, true, LocalDateTime.now());
    }
}
