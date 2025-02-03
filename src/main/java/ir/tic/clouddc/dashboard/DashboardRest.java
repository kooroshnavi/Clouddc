package ir.tic.clouddc.dashboard;


import io.netty.handler.ssl.SslContext;
import org.apache.commons.codec.EncoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRest {

    private final SslContext sslContext;

    @Autowired
    public DashboardRest(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @GetMapping("/greeting")
    public String helloWorld() throws FileNotFoundException {

        return "Hello World";
    }

    @GetMapping("/ceph")
    public String getApiResult() throws EncoderException {
        HttpClient httpClient = HttpClient
                .create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext));
        ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);
        var uriBuilderFactory = new DefaultUriBuilderFactory();
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        WebClient client = WebClient.builder()
                .uriBuilderFactory(uriBuilderFactory)
                //   .defaultCookie("cookieKey", "cookieValue")
                //       .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                //     .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8080"))
                .clientConnector(clientHttpConnector)
                .build();

        // WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.GET);
        //WebClient.RequestBodySpec bodySpec = uriSpec.uri("query=ceph_cluster_total_bytes{namespace='prod-ceph'}" );
        //  WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue("data");
        var filterValue = "{namespace='prod-ceph'}";

        return client
                .get()
                .uri("https://monitoring.it.tic.ir/ts-p/api/v1/query?query=ceph_cluster_total_bytes{filter}", filterValue)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
