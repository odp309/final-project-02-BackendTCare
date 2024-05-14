package com.bni.finproajubackend.controller.middleware;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;

@Configuration
@EnableWebMvc
public class WebConfiguration {
    private static final Long MAX_AGE = 3600L;
    private static final int CORS_FILTER_ORDER = -100;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // allow backend to receive headers that contains auth info
        config.setAllowCredentials(true);

        // path to frontend
        config.addAllowedOrigin("http://localhost:4200");

        // typical headers backend must accept
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT));

        // custom headers backend must accept
        config.addAllowedHeader("No-Auth");

        // methods backend must accept
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name()));

        // time the cors config is accepted => 30 minutes
        config.setMaxAge(MAX_AGE);

        // apply config to all routes
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
