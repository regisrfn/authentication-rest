package com.rufino.server.config;

import static com.rufino.server.constant.SecurityConst.JWT_TOKEN_HEADER;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class CorsConfig {

    private Dotenv dotenv;
    
    @Autowired
    public CorsConfig(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    @Bean
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(getAllowedUrls());
		
		corsConfiguration.setAllowedHeaders(Arrays.asList(
				"Origin", 
				"Access-Control-Allow-Origin", 
				"Content-Type",
				"Accept", 
				"Authorization", 
				"Origin, Accept", 
				"X-Requested-With", 
				"Access-Control-Request-Method",
				"Access-Control-Request-Headers", 
				JWT_TOKEN_HEADER)
		);
		
		corsConfiguration.setExposedHeaders(Arrays.asList(
				"Origin", 
				"Content-Type", 
				"Accept", 
				"Authorization",
				"Access-Control-Allow-Origin",
				"Access-Control-Allow-Credentials", 
				JWT_TOKEN_HEADER)
		);

		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

    private List<String> getAllowedUrls(){
        String URL = dotenv.get("ALLOWED_ORIGINS_URL");
        String[] urls = URL.split(",");
        return Arrays.asList(urls);
    }
    
}
