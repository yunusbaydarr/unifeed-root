package app.unifeed.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean OpenAPI unifeedOpenApi() {
        return new OpenAPI().info(new Info().title("UniFeed API").version("v1"))
            .schemaRequirement("bearerAuth", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
