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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable()
                .formLogin()
                .authenticationDetailsSource(authenticationDetailsSource)
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler())
                .and()
                .addFilterAfter(new JwtTokenVerifier(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers(headers -> headers
                        .contentTypeOptions(withDefaults())
                        .xssProtection(withDefaults())
                        .cacheControl(withDefaults())
                        .httpStrictTransportSecurity(withDefaults())
                        .frameOptions(withDefaults()
                        ))
                .authorizeRequests()
                .antMatchers("/index", "/css/*", "/js/*", "/swagger-ui.html").permitAll()
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
