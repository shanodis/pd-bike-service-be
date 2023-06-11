package me.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Klasa konfiguracyjna CorsConfiguration definiuje ustawienia obsługi żądań CORS (Cross-Origin Resource Sharing).
 * Umożliwia komunikację między różnymi domenami/serwerami.
 */
@Configuration
public class CorsConfiguration {

    /**
     * Definiuje filtr CorsFilter, który obsługuje żądania CORS.
     *
     * @return Obiekt CorsFilter, który implementuje obsługę żądań CORS.
     */
    @Bean
    public CorsFilter corsFilter() {
        org.springframework.web.cors.CorsConfiguration corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type", "Accept",
                "Authorization", "Origin , Accept", "X-Requested-With", "Access-Control-Request-Method", "Access-Control-Request-Headers",
                "Authorization-Refresh"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept",
                "Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials",
                "Authorization-Refresh"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
