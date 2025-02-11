package ir.tic.clouddc.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.tic.clouddc.api.ApiResponseService;
import ir.tic.clouddc.api.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

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
    List<DashboardResult> dashboardResultList;

    private final ApiResponseService apiResponseService;

    @Autowired
    public DashboardServiceImpl(WebClient webClient, ApiResponseService apiResponseService) {
        this.webClient = webClient;
        this.apiResponseService = apiResponseService;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('API_GET_AUTH')")
    public Response getCephClusterResponseList() throws JsonProcessingException {
        dashboardResultList = new ArrayList<>();

        // 1. Total Capacity
        dashboardResultList.add(fetchAndPrepareResponse(0, CEPH_CLUSTER_TOTAL_CAPACITY_QUERY, CEPH_CLUSTER_FILTER, "حجم کل کلاستر"));

        // 2. Used Capacity
        dashboardResultList.add(fetchAndPrepareResponse(1, CEPH_CLUSTER_USED_CAPACITY_QUERY, CEPH_CLUSTER_FILTER, "حجم مصرف شده کلاستر"));

        return apiResponseService.createResponse("آمار استوریج سبزسیستم - CEPH", dashboardResultList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('API_GET_AUTH')")
    public Response getCephMessengerUsageResponseList() throws JsonProcessingException {
        dashboardResultList = new ArrayList<>();
        int counter = 0;
        do {
            var response = fetchAndPrepareResponse(
                    counter,
                    MESSENGER_USAGE_QUERY,
                    CEPH_USAGE_MESSENGER_FILTER.get(counter),
                    CEPH_USAGE_MESSENGER_TITLE.get(counter));
            dashboardResultList.add(response);
            counter += 1;
        } while (counter < 4);

        return apiResponseService.createResponse( "استوریج سبزسیستم - حجم مصرفی پیام رسان ها", dashboardResultList);
    }


    private DashboardResult fetchAndPrepareResponse(int id, String query, String filterValue, String title) throws JsonProcessingException {
        CephApiData apiData = apiCall(query, filterValue);
        var response = makeResponseValue(apiData);
        response.setId(id + 1);
        response.setTitle(title);

        return response;
    }

    private DashboardResult makeResponseValue(CephApiData apiData) {
        DashboardResult dashboardResult = new DashboardResult();
        String bytesString = apiData
                .getData()
                .getResult()
                .get(0)
                .getValue()
                .get(1)
                .toString();
        BigDecimal output = new BigDecimal(bytesString);
        BigDecimal petaBytes = output.divide(new BigDecimal("1000").pow(5), 3, RoundingMode.HALF_UP);
        dashboardResult.setValue(String.valueOf(petaBytes));
        BigDecimal displayValue;

        if (petaBytes.compareTo(BigDecimal.ONE) >= 0) {
            displayValue = petaBytes;
            dashboardResult.setUnit("PB");
        } else {
            BigDecimal divide = new BigDecimal("1").divide(new BigDecimal("1024").pow(1), RoundingMode.HALF_UP);
            if (petaBytes.compareTo(new BigDecimal("1024")) < 0 && petaBytes.compareTo(BigDecimal.ONE) < 0 && petaBytes.compareTo(divide) >= 0) {
                displayValue = petaBytes.multiply(new BigDecimal("1024"));
                dashboardResult.setUnit("TB");
            } else if (petaBytes.compareTo(divide) < 0 && petaBytes.compareTo(new BigDecimal("1").divide(new BigDecimal("1024").pow(2), RoundingMode.HALF_UP)) >= 0) {
                displayValue = petaBytes.multiply(new BigDecimal("1024").pow(2));
                dashboardResult.setUnit("GB");
            } else {
                displayValue = petaBytes.multiply(new BigDecimal("1024").pow(3));
                dashboardResult.setUnit("KB");
            }
        }
        DecimalFormat df = new DecimalFormat("0.0");
        String formattedValue = df.format(displayValue);
        dashboardResult.setValue(formattedValue);

        return dashboardResult;
    }

    private CephApiData apiCall(String query, String filterValue) throws JsonProcessingException {
        String jsonResponse = webClient
                .get()
                .uri(BASE_URL + query, filterValue)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(jsonResponse, CephApiData.class);
    }
}
