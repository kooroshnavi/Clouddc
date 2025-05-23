package ir.tic.clouddc.security;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.net.ssl.KeyManagerFactory;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;

@Configuration
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final NotificationService notificationService;

    private final OTPFailureHandler otpFailureHandler;

    private final HeaderWriterLogoutHandler clearSiteData;

    private final PersonService personService;

    private final ResourceLoader resourceLoader;

    @Autowired
    public SecurityConfig(NotificationService notificationService, OTPFailureHandler otpFailureHandler, PersonService personService, ResourceLoader resourceLoader) {
        this.personService = personService;
        this.resourceLoader = resourceLoader;
        this.clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.ALL));
        this.notificationService = notificationService;
        this.otpFailureHandler = otpFailureHandler;
    }


    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(matcherRegistry ->
                        matcherRegistry.anyRequest().authenticated())
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new RestAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain mvcSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .permitAll()
                        .failureHandler(otpFailureHandler)
                        .successHandler((request, response, authentication) -> {
                            var localDate = UtilService.getDATE();
                            var localTime = UtilService.getTime();
                            response.sendRedirect("/");
                            String address = personService.getPersonAddressByUsername(authentication.getName());

                            notificationService.sendSuccessLoginMessage(
                                    address
                                    , request.getRemoteAddr()
                                    , LocalDateTime.of(localDate, localTime));

                            personService.registerLoginHistory(address
                                    , request.getRemoteAddr()
                                    , true);
                        })
                )

                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry())
                )

                .logout(logout -> logout
                        .addLogoutHandler(clearSiteData)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                // other configuration options;

                .authorizeHttpRequests(authCustomizer -> authCustomizer
                        .requestMatchers("/login/**")
                        .permitAll()
                        .requestMatchers("/otp/**")
                        .permitAll()
                        .requestMatchers("/panel/**")
                        .permitAll()
                        .requestMatchers("/fonts/**")
                        .permitAll()
                        .requestMatchers("/error/**")
                        .permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SslContext sslContext() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, IOException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(resourceLoader.getResource("classpath:tic-client-certificate.p12").getInputStream(), "292odzr4".toCharArray());
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, "292odzr4".toCharArray());

        return SslContextBuilder.forClient()
                .keyManager(keyManagerFactory)
                .build();
    }
}
