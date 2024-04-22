package com.ojas.multitenant.config;

import com.ojas.multitenant.security.jwt.JWTFilter;
import com.ojas.multitenant.multi_tenancy.filter.TenantFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            TenantFilter tenantContextFilter
    ) throws Exception {
        return http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(tenantContextFilter, JWTFilter.class)
                .build();
    }
}
