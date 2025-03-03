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

    private static final String CEPH_CLUSTER_FILTER = "{namespace='prod-ceph'}/1000/1000/1000/1000/1000";

    private static final String MESSENGER_USAGE_QUERY = "?query=sum(radosgw_usage_bucket_bytes{CEPH_USAGE_FILTER})";

    private static final List<String> CEPH_USAGE_MESSENGER_FILTER_1 = Arrays.asList(
            "{cluster='ceph',owner='bale',service='prod-ceph-rgw-exporter-brp'}*3",
            "{cluster='ceph',owner='soroush',service='prod-ceph-rgw-exporter-brp'}*3",
            "{cluster='ceph',owner='gap',service='prod-ceph-rgw-exporter-brp'}*3",
            "{cluster='ceph',owner='igap',service='prod-ceph-rgw-exporter-brp'}*3");

    private static final List<String> CEPH_USAGE_MESSENGER_FILTER_2 = Arrays.asList(
            "{cluster='ceph',owner='bale',service='prod-ceph-rgw-exporter-bec'}*1.5",
            "{cluster='ceph',owner='soroush',service='prod-ceph-rgw-exporter-bec'}*1.5",
            "{cluster='ceph',owner='gap',service='prod-ceph-rgw-exporter-bec'}*1.5",
            "{cluster='ceph',owner='igap',service='prod-ceph-rgw-exporter-bec'}*1.5");

    private static final List<String> CEPH_USAGE_MESSENGER_TITLE = Arrays.asList(
            "داده ورزی سداد فارس (بله)",
            "ستاک هوشمند شریف (سروش پلاس)",
            "فناوری اطلاعات توسعه سامان (گپ)",
            "آی گپ");

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
            DecimalFormat clusterFormat = new DecimalFormat("0.0");
            ((CephResult) totalCapacity).setValue(clusterFormat.format(Float.valueOf(((CephResult) totalCapacity).getValue())));
            ((CephResult) usedCapacity).setValue(clusterFormat.format(Float.valueOf(((CephResult) usedCapacity).getValue())));

            return new Response("OK"
                    , "استوریج ابری سبزسیستم"
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
            var brpResult = fetchAndPrepareResult(
                    counter,
                    MESSENGER_USAGE_QUERY,
                    CEPH_USAGE_MESSENGER_FILTER_1.get(counter),
                    CEPH_USAGE_MESSENGER_TITLE.get(counter));

            var becResult = fetchAndPrepareResult(
                    counter,
                    MESSENGER_USAGE_QUERY,
                    CEPH_USAGE_MESSENGER_FILTER_2.get(counter),
                    CEPH_USAGE_MESSENGER_TITLE.get(counter));

            if (brpResult instanceof CephResult brpDataResult && becResult instanceof CephResult becDataResult) {
                CephResult messengerUsageResult = getMessengerUsageResult(brpDataResult, becDataResult);
                cephDataResultList.add(messengerUsageResult);

                counter += 1;
            } else {
                assert brpResult instanceof ErrorResult;
                return new Response("Error"
                        , "خطا در دریافت اطلاعات"
                        , UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime())
                        , List.of((ErrorResult) brpResult));
            }

        } while (counter < 4);

        return new Response("OK"
                ,
                "استوریج ابری سبزسیستم - حجم مصرفی پیام رسان ها"
                , UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime())
                , cephDataResultList);
    }

    private static CephResult getMessengerUsageResult(CephResult brpDataResult, CephResult becDataResult) {
        CephResult messengerUsageResult = new CephResult();
        float finalValueResult = (float) ((Float.parseFloat(brpDataResult.getValue()) + Float.parseFloat(becDataResult.getValue())) / (Math.pow(1000, 5)));

        if (finalValueResult >= 1) {
            DecimalFormat pbFormat = new DecimalFormat("0.00");
            messengerUsageResult.setUnit("PB");
            messengerUsageResult.setValue(pbFormat.format(finalValueResult));

        } else {
            finalValueResult *= 1000;
            DecimalFormat tbFormat = new DecimalFormat("0");
            if (finalValueResult >= 1) {
                messengerUsageResult.setValue(tbFormat.format(finalValueResult));
                messengerUsageResult.setUnit("TB");
            } else {
                finalValueResult *= 1000;
                if (finalValueResult >= 1) {
                    messengerUsageResult.setUnit("GB");
                    messengerUsageResult.setValue(tbFormat.format(finalValueResult));
                } else {
                    messengerUsageResult.setUnit("< 1 GB");
                    messengerUsageResult.setValue("حجم مصرفی کمتر از یک گیگابایت می باشد");
                }
            }
        }

        messengerUsageResult.setTitle(brpDataResult.getTitle());
        messengerUsageResult.setId(brpDataResult.getId());

        return messengerUsageResult;
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
        BigDecimal petaBytes = new BigDecimal(bytesString);
        cephDataResult.setValue(String.valueOf(petaBytes));
        BigDecimal displayValue;

        if (petaBytes.compareTo(BigDecimal.ONE) >= 0) {
            displayValue = petaBytes;
            cephDataResult.setUnit("PB");
        } else {
            BigDecimal divide = new BigDecimal("1").divide(new BigDecimal("1000").pow(1), RoundingMode.HALF_UP);
            if (petaBytes.compareTo(new BigDecimal("1000")) < 0 && petaBytes.compareTo(BigDecimal.ONE) < 0 && petaBytes.compareTo(divide) >= 0) {
                displayValue = petaBytes.multiply(new BigDecimal("1000"));
                cephDataResult.setUnit("TB");
            } else {
                displayValue = petaBytes.multiply(new BigDecimal("1000").pow(2));
                cephDataResult.setUnit("GB");
            }
        }
        DecimalFormat df = new DecimalFormat("000000000000000.000000000000000");
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
