package me.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.project.auth.CustomWebAuthenticationDetailsSource;
import me.project.auth.formLogin.FormLoginHelper;
import me.project.auth.jwt.JwtTokenVerifier;
import me.project.service.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final CustomWebAuthenticationDetailsSource authenticationDetailsSource;

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
                )
        ;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new FormLoginHelper(userService, new ObjectMapper());
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new FormLoginHelper(userService, new ObjectMapper());
    }
}
