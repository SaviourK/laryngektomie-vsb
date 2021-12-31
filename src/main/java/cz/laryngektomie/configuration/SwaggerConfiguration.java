package cz.laryngektomie.configuration;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean("customSwaggerConfiguration")
    public Docket swaggerConfiguration() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .build()
                .consumes(supportedMediaTypes())
                .produces(supportedMediaTypes())
                .apiInfo(createApiInfo());
    }

    private ApiInfo createApiInfo() {
        return new ApiInfo(
                "Laryngektomie API",
                "Laryngektomie API Descr",
                "1.0",
                "Free to use",
                new Contact("Vítězslav Kaňok", "https://vk.cz", "vitezslav.kanok@email.cz"),
                "API License",
                "https://vk.cz",
                Collections.emptyList()
        );
    }

    public Set<String> supportedMediaTypes() {
        return Collections.singleton(MediaType.APPLICATION_JSON_VALUE);
    }
}
