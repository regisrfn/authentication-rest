package com.rufino.server.config;

import java.util.Collections;

import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;



@Configuration
@EnableSwagger2
public class SwaggerConfig {

    public Docket authenticationApiDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .apiInfo(metaInfo());
    }

    private ApiInfo metaInfo(){
        ApiInfo apiInfo = new ApiInfo(
            "User management REST API", 
            "An rest api with authentication for user management", 
            "1.0", 
            "Terms of Service", 
            new Contact(
                "Regis Rufino",
                "https://github.com/regisrfn",
                "regis.rfnrodrigues@gmail.com"
            ),
            "Apache License Version 2.0", 
            "https://www.apache.org/licenses/LICENSE-2.0", 
            Collections.emptyList()
        );
        return apiInfo;
    }
    
}
