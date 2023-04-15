package me.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

@Configuration
public class OptionalConfiguration {

    @Bean
    public Jsonb jsonb(){
        return JsonbBuilder.create(
                new JsonbConfig()
        );
    }
}
