package me.project.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.AllArgsConstructor;
import me.project.auth.CustomWebAuthenticationDetailsSource;
import me.project.auth.formLogin.FormLoginHelper;
import me.project.auth.jwt.JwtTokenVerifier;
import me.project.service.user.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableEncryptableProperties
@AllArgsConstructor
@EnableWebSecurity
@EnableWebMvc
@EnableScheduling
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        FormLoginHelper formLoginHelper = new FormLoginHelper(userService);
        http.cors()
                .and()
                .csrf().disable()
                .formLogin()
                .authenticationDetailsSource(authenticationDetailsSource)
                .successHandler(formLoginHelper)
                .and()
                .httpBasic()
                .and()
                .addFilterAfter(new JwtTokenVerifier(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/index", "/css/*", "/js/*", "/swagger-ui.html").permitAll()
        ;
    }
}
