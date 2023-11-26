package com.navi.dcim.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        //    .requestMatchers("/login")
                        .requestMatchers("/assignForm/**").permitAll()
                        .requestMatchers("/dashboard/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        //  .requestMatchers("/assignForm/**").permitAll()
                        .anyRequest().authenticated()

                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/app/main")
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
