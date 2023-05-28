package me.project.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Configuration
@RestController
@RequestMapping(path = "/")
public class SwaggerConfiguration {

    @Bean
    public GroupedOpenApi publicApi(){
        return GroupedOpenApi.builder()
                .group("frontend")
                .pathsToMatch(
                        "/api/v1/auth/**",
                        "/api/v1/bikes/**",
                        "/api/v1/users/**",
                        "/api/v1/dictionaries/**",
                        "/api/v1/services/**"
                )
                .pathsToExclude("/")
                .build();
    }

    @Bean
    public GroupedOpenApi devApi() {
        return GroupedOpenApi.builder()
                .group("all-developer")
                .pathsToMatch("/**")
                .pathsToExclude("/")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API v1")
                        .description("Simple API")
                        .version("v0.2")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Simple API Wiki Documentation")
                        .url("https://github.com/Paros998"));
    }

    @GetMapping
    public RedirectView redirectView(){
        return new RedirectView("/swagger-ui.html");
    }
}
