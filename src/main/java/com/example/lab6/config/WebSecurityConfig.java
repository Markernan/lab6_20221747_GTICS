package com.example.lab6.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {

        return (request, response, authentication) -> {
            String redirectURL = "/";


            if (authentication.getAuthorities().stream()

                    .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
                redirectURL = "/admin";

            } else if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("USUARIO"))) {
                redirectURL = "/";

            }

            response.sendRedirect(redirectURL);

        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz

                        .requestMatchers("/", "/heroes", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()

                        .requestMatchers("/admin/**").hasAuthority("ADMIN")

                        .requestMatchers("/usuario/**").hasAuthority("USUARIO")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")

                        .successHandler(successHandler())

                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")

                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}