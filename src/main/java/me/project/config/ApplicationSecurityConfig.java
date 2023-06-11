package me.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.project.auth.CustomWebAuthenticationDetailsSource;
import me.project.auth.formLogin.FormLoginHelper;
import me.project.auth.jwt.JwtTokenVerifier;
import me.project.service.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Klasa konfiguracyjna ApplicationSecurityConfig definiuje ustawienia bezpieczeństwa aplikacji.
 * Rozszerza klasę WebSecurityConfigurerAdapter, co umożliwia dostosowanie konfiguracji zabezpieczeń dla aplikacji webowej.
 */
@Configuration
@AllArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    // Stała przechowująca listę ścieżek, które są wyłączone z autoryzacji
    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            // -- Rest of endpoints
            "/login/**",
            "/api/v1/auth/**"
    };

    /**
     * Konfiguracja zabezpieczeń HTTP.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers("/**").authenticated() // require authentication for any endpoint that's not whitelisted
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin()
                .authenticationDetailsSource(authenticationDetailsSource)
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler())
                .and()
                .addFilterAfter(new JwtTokenVerifier(), UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers
                        .contentTypeOptions(withDefaults())
                        .xssProtection(withDefaults())
                        .cacheControl(withDefaults())
                        .httpStrictTransportSecurity(withDefaults())
                        .frameOptions(withDefaults())
                );
    }

    /**
     * Definiuje bean AuthenticationFailureHandler.
     *
     * @return Obiekt AuthenticationFailureHandler, który obsługuje błędy uwierzytelniania.
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new FormLoginHelper(userService, new ObjectMapper());
    }

    /**
     * Definiuje bean AuthenticationSuccessHandler.
     *
     * @return Obiekt AuthenticationSuccessHandler, który obsługuje poprawne uwierzytelnianie.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new FormLoginHelper(userService, new ObjectMapper());
    }
}
