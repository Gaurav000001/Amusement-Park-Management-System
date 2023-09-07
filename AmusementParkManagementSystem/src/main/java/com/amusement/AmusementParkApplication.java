package com.amusement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;


@OpenAPIDefinition
@SpringBootApplication
public class AmusementParkApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmusementParkApplication.class, args);
	}
	
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.components(new Components().addSecuritySchemes("bearer-key",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
							.info(new Info().title("Amusement Park").version("1.0").description("Amusement Park Application")
									.termsOfService("8888/swagger-ui/index.html").license(new License().name("Apache 2.0")));
	}
	
	
	
	@Bean
	public LocalValidatorFactoryBean validator(MessageSource ms) {
		
		LocalValidatorFactoryBean lvfb=new LocalValidatorFactoryBean();
		lvfb.setValidationMessageSource(ms);
		
		return lvfb;
		
	}
}

