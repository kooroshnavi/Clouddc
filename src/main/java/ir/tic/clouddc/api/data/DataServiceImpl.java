package ir.tic.clouddc.api.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.tic.clouddc.api.response.ErrorResult;
import ir.tic.clouddc.api.response.Response;
import ir.tic.clouddc.api.response.Result;
import ir.tic.clouddc.utils.UtilService;
import jakarta.servlet.UnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {

    private final WebClient webClient;

    private static final String BASE_URL = "https://monitoring.it.tic.ir/ts-p/api/v1/query";

    private static final String CEPH_CLUSTER_TOTAL_CAPACITY_QUERY = "?query=ceph_cluster_total_bytes{filter}";

    private static final String CEPH_CLUSTER_USED_CAPACITY_QUERY = "?query=ceph_cluster_total_used_raw_bytes{filter}";

    private static final String CEPH_CLUSTER_FILTER = "{namespace='prod-ceph'}";

    private static final String MESSENGER_USAGE_QUERY = "?query=sum(radosgw_usage_bucket_bytes{CEPH_USAGE_FILTER})*10";

    private static final List<String> CEPH_USAGE_MESSENGER_FILTER = Arrays.asList(
            "{cluster='ceph',owner='bale',service='prod-ceph-rgw-exporter-brp'}",
            "{cluster='ceph',owner='soroush',service='prod-ceph-rgw-exporter-brp'}",
            "{cluster='ceph',owner='gap',service='prod-ceph-rgw-exporter-brp'}",
            "{cluster='ceph',owner='igap',service='prod-ceph-rgw-exporter-brp'}");

    private static final List<String> CEPH_USAGE_MESSENGER_TITLE = Arrays.asList(
            "بله",
            "سروش پلاس",
            "گپ",
            "آیگپ");

    List<CephResult> cephDataResultList;

    @Autowired
    public DataServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('API_GET_AUTH')")
    public Response getCephClusterDataResponse() throws JsonProcessingException {
        cephDataResultList = new ArrayList<>();
        Result totalCapacity = fetchAndPrepareResult(0, CEPH_CLUSTER_TOTAL_CAPACITY_QUERY, CEPH_CLUSTER_FILTER, "حجم کل");
        Result usedCapacity = fetchAndPrepareResult(1, CEPH_CLUSTER_USED_CAPACITY_QUERY, CEPH_CLUSTER_FILTER, "حجم مصرفی");

        if (totalCapacity instanceof CephResult && usedCapacity instanceof CephResult) {
            return new Response("OK"
                    , "اطلاعات کلاستر استوریج سبزسیستم - CEPH"
                    , UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime())
                    , List.of(totalCapacity, usedCapacity));
        }

        return new Response("Error"
                , "خطا در دریافت اطلاعات"
                , UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now())
                , List.of(new ErrorResult("وب سرویس ریموت سامانه مانیتورینگ جهت دریافت اطلاعات در دسترس نمی باشد")));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('API_GET_AUTH')")
    public Response getCephMessengerUsageDataResponse() throws JsonProcessingException {
        cephDataResultList = new ArrayList<>();
        int counter = 0;
        do {
            var dataResult = fetchAndPrepareResult(
                    counter,
                    MESSENGER_USAGE_QUERY,
                    CEPH_USAGE_MESSENGER_FILTER.get(counter),
                    CEPH_USAGE_MESSENGER_TITLE.get(counter));
            if (dataResult instanceof CephResult cephDataResult) {
                cephDataResultList.add(cephDataResult);
                counter += 1;
            } else {
                return new Response("Error"
                        , "خطا در دریافت اطلاعات"
                        , UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime())
                        , List.of((ErrorResult) dataResult));
            }

        } while (counter < 4);

        return new Response("OK"
                ,
                "استوریج سبزسیستم - حجم مصرفی پیام رسان ها"
                , UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime())
                , cephDataResultList);
    }

    private Result fetchAndPrepareResult(int id, String query, String filterValue, String title) throws JsonProcessingException {
        var remoteResult = grabRemoteData(query, filterValue);
        if (remoteResult instanceof RemoteResult remoteValidData) {
            var cephDataResult = makeResponseValue(remoteValidData);
            cephDataResult.setId(id + 1);
            cephDataResult.setTitle(title);

            return cephDataResult;
        }

        return remoteResult;
    }

    private CephResult makeResponseValue(RemoteResult remoteValidData) {
        CephResult cephDataResult = new CephResult();
        String bytesString = remoteValidData
                .getData()
                .getResult()
                .get(0)
                .getValue()
                .get(1)
                .toString();
        BigDecimal output = new BigDecimal(bytesString);
        BigDecimal petaBytes = output.divide(new BigDecimal("1000").pow(5), 3, RoundingMode.HALF_UP);
        cephDataResult.setValue(String.valueOf(petaBytes));
        BigDecimal displayValue;

        if (petaBytes.compareTo(BigDecimal.ONE) >= 0) {
            displayValue = petaBytes;
            cephDataResult.setUnit("PB");
        } else {
            BigDecimal divide = new BigDecimal("1").divide(new BigDecimal("1024").pow(1), RoundingMode.HALF_UP);
            if (petaBytes.compareTo(new BigDecimal("1024")) < 0 && petaBytes.compareTo(BigDecimal.ONE) < 0 && petaBytes.compareTo(divide) >= 0) {
                displayValue = petaBytes.multiply(new BigDecimal("1024"));
                cephDataResult.setUnit("TB");
            } else if (petaBytes.compareTo(divide) < 0 && petaBytes.compareTo(new BigDecimal("1").divide(new BigDecimal("1024").pow(2), RoundingMode.HALF_UP)) >= 0) {
                displayValue = petaBytes.multiply(new BigDecimal("1024").pow(2));
                cephDataResult.setUnit("GB");
            } else {
                displayValue = petaBytes.multiply(new BigDecimal("1024").pow(3));
                cephDataResult.setUnit("KB");
            }
        }
        DecimalFormat df = new DecimalFormat("0.0");
        String formattedValue = df.format(displayValue);
        cephDataResult.setValue(formattedValue);

        return cephDataResult;
    }

    private Result grabRemoteData(String query, String filterValue) throws JsonProcessingException {
        String jsonResponse;
        jsonResponse = webClient
                .get()
                .uri(BASE_URL + query, filterValue)
                .retrieve()
                .onStatus(httpStatusCode -> !httpStatusCode.is2xxSuccessful(), clientResponse -> remoteErrorHandler())
                .bodyToMono(String.class)
                .block();
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(jsonResponse, RemoteResult.class);
    }

    private Mono<? extends Throwable> remoteErrorHandler() {
        return Mono.error(new UnavailableException("Remote API Unavailable"));
    }
}
