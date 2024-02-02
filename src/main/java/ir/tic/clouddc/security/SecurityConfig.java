package ir.tic.clouddc.security;

import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDateTime;

@Configuration
@EnableMethodSecurity
@Slf4j
class SecurityConfig {

    private final NotificationService notificationService;
    private final OtpFailureHandler otpFailureHandler;
    private final PersonService personService;

    @Autowired
    public SecurityConfig(NotificationService notificationService, OtpFailureHandler otpFailureHandler, PersonService personService) {
        this.notificationService = notificationService;
        this.otpFailureHandler = otpFailureHandler;
        this.personService = personService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .permitAll()
                        .failureHandler(otpFailureHandler)
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/");
                            notificationService.sendSuccessLoginMessage(
                                    personService.getAuthenticatedPersonId()
                                    , request.getRemoteAddr()
                                    , LocalDateTime.now());
                        })
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
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

}
