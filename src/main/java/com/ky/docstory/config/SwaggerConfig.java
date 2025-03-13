package com.ky.docstory.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        servers = {
                @Server(url = "http://localhost:8080", description = "DocStory local 서버")
        }
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI DocStoryOpenAPI() {
        String key = "JWT Token (Bearer)";

        return new OpenAPI()
                .info(apiInfo())
                .components(new Components().addSecuritySchemes(key, apiKey()))
                .addSecurityItem(new SecurityRequirement().addList(key));
    }

    private Info apiInfo() {
        return new Info()
                .title("DocStory")
                .description("DocStory의 API 문서")
                .version("1.0.0");
    }

    private SecurityScheme apiKey() {
        return new SecurityScheme()
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .type(SecurityScheme.Type.HTTP)
                .name(HttpHeaders.AUTHORIZATION);
    }
}
