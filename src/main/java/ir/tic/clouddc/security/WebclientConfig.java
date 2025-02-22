package ir.tic.clouddc.security;

import io.netty.handler.ssl.SslContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

@Component
public class WebclientConfig {

    private final SslContext sslContext;

    @Autowired
    public WebclientConfig(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient
                .create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext));

        ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);

        var uriBuilderFactory = new DefaultUriBuilderFactory();
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        return WebClient.builder()
                //   .baseUrl(BASE_URL)
                .uriBuilderFactory(uriBuilderFactory)
                //      .baseUrl(BASE_URL)
                //   .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                //     .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8080"))
                .clientConnector(clientHttpConnector)
                .build();
    }
}
