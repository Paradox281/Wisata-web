package com.altura.altura.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI alturaOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("https://altura.up.railway.app/"))
                .info(new Info()
                        .title("Altura API Documentation")
                        .description("API documentation for Altura Tourism Application")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Altura Team")
                                .email("contact@altura.com")));
    }
}