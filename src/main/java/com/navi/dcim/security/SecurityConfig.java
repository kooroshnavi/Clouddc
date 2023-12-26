package com.navi.dcim.security;

import com.navi.dcim.notification.NotificationService;
import com.navi.dcim.person.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {


    private final NotificationService notificationService;
    @Autowired
    public SecurityConfig(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {

        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/");
                            log.info(request.getRemoteAddr());
                         /*   notificationService.(
                                    authentication.getName()
                                    , request.getRemoteAddr()
                                    , LocalDateTime.now());*/
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()// If the user fails to login, application will redirect the user to this endpoint
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
                        .requestMatchers(mvcMatcherBuilder.pattern("login/**"))
                        .permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("assignForm/**"))
                        .permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("dashboard/**"))
                        .permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("fonts/**"))
                        .permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public UserDetailsManager userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
