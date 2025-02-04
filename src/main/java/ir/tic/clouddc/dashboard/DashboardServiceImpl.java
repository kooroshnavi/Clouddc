package ir.tic.clouddc.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String CEPH_TOTAL_CAPACITY_QUERY = "?query=ceph_cluster_total_bytes{filter}";

    private static final String CEPH_USED_CAPACITY_QUERY = "?query=ceph_cluster_total_used_raw_bytes{filter}";

    private static final String Ceph_Filter_Value_1 = "{namespace='prod-ceph'}";

    private static final int CEPH_TOTAL_CAPACITY_ID = 1;

    private static final int CEPH_USED_CAPACITY_ID = 2;

    private static final List<String> messengerList = Arrays.asList("bale", "soroush", "gap", "igap");

    private static final String MESSENGER_USAGE_QUERY_1 = "?query=sum(radosgw_usage_bucket_bytes{cluster=\"ceph\",owner=\"bale\",service=\"prod-ceph-rgw-exporter-brp\"})*1.5";

    private static final String MESSENGER_USAGE_QUERY_2 = "?query=sum(radosgw_usage_bucket_bytes{filter2})";

    private static final String Ceph_Filter_Value_2 = "{cluster='ceph',owner='bale',service='prod-ceph-rgw-exporter-brp'}";

    @Autowired
    public DashboardServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<Response> getCephResponseList() throws JsonProcessingException {

        List<Response> cephResponseList = new ArrayList<>();

        // 1. Total Capacity
        //  cephResponseList.add(fetchAndPrepareResponse(CEPH_TOTAL_CAPACITY_ID, CEPH_TOTAL_CAPACITY_QUERY, Ceph_Filter_Value_1, "حجم کل کلاستر ذخیره سازی سبزسیستم"));

        // 2. Used Capacity
        //  cephResponseList.add(fetchAndPrepareResponse(CEPH_USED_CAPACITY_ID, CEPH_USED_CAPACITY_QUERY, Ceph_Filter_Value_1, "حجم مصرف شده کلاستر ذخیره سازی سبزسیستم"));

        // 3. messenger usage
        for (int i = 0; i < 1; i++) {
            String apiResponse = usageApiCall(i, MESSENGER_USAGE_QUERY_1, Ceph_Filter_Value_2);
            CephApiData apiData = modelExtract(apiResponse);
            String resultValue1 = makeResponseValue(apiData);
            cephResponseList.add(new Response(i + 3, "title", resultValue1, "PB"));
            String apiResponse2 = usageApiCall(i, MESSENGER_USAGE_QUERY_2, Ceph_Filter_Value_2);
            CephApiData apiData2 = modelExtract(apiResponse2);
            String resultValue2 = makeResponseValue(apiData);
            cephResponseList.add(new Response(i + 3, "title", resultValue2, "PB"));

        }

        return cephResponseList;
    }

    private String usageApiCall(int i, String messengerUsageQuery1, String cephFilterValue2) {
        return webClient
                .get()
                .uri(BASE_URL + messengerUsageQuery1, cephFilterValue2, messengerList.get(i))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private Response fetchAndPrepareResponse(int id, String query, String filterValue, String title) throws JsonProcessingException {

        String apiResponse = apiCall(query, filterValue);

        CephApiData apiData = modelExtract(apiResponse);

        String resultValue = makeResponseValue(apiData);

        return new Response(id, title, resultValue, "PB");
    }

    private String makeResponseValue(CephApiData apiData) {
        String bytesString = apiData
                .getData()
                .getResult()
                .get(0)
                .getValue()
                .get(1)
                .toString();
        BigDecimal petaBytes = new BigDecimal(bytesString);
        BigDecimal finalValue = petaBytes.divide(new BigDecimal("1000").pow(5), 1, RoundingMode.HALF_UP);
        DecimalFormat df = new DecimalFormat("00.0");

        return bytesString;
    }

    private String apiCall(String query, String filterValue) {
        return webClient
                .get()
                .uri(BASE_URL + query, filterValue)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private static CephApiData modelExtract(String jsonResponse) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(jsonResponse, CephApiData.class);
    }
}
