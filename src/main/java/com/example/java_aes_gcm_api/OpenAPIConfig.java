package com.example.java_aes_gcm_api;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Java AES GCM API")
                        .version("1.0")
                        .description("A demo API for AES GCM encryption/decryption")
                        .contact(new Contact().name("Ian Cheng").email("dunz.zheng@gmail.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local server"),
                        new Server().url("http://35.212.146.250:8080").description("GCP server"),
                        new Server().url("https://java-aes-gcm-api.onrender.com").description("Render server")
                ));
    }
}
