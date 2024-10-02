package ir.tic.clouddc.security;

import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.time.LocalDateTime;

@Configuration
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final NotificationService notificationService;

    private final OTPFailureHandler otpFailureHandler;

    private final HeaderWriterLogoutHandler clearSiteData;

    private final PersonService personService;

    @Autowired
    public SecurityConfig(NotificationService notificationService, OTPFailureHandler otpFailureHandler, PersonService personService) {
        this.personService = personService;
        this.clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.ALL));
        this.notificationService = notificationService;
        this.otpFailureHandler = otpFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .permitAll()
                        .failureHandler(otpFailureHandler)
                        .successHandler((request, response, authentication) -> {
                            var localDate = UtilService.getDATE();
                            var localTime = UtilService.getTime();
                            response.sendRedirect("/");
                            notificationService.sendSuccessLoginMessage(
                                    authentication.getName()
                                    , request.getRemoteAddr()
                                    , LocalDateTime.of(localDate, localTime));
                            personService.registerLoginHistory(personService
                                            .getPersonByUsername(authentication.getName()).getAddress().getValue()
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
                // other configuration options
                .authorizeHttpRequests(authCustomizer -> authCustomizer
                        .requestMatchers("login/**")
                        .permitAll()
                        .requestMatchers("otp/**")
                        .permitAll()
                        .requestMatchers("panel/**")
                        .permitAll()
                        .requestMatchers("fonts/**")
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
}
