package io.nutritionapp.datapipeline.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spoonacular")
@Getter
@Setter
public class SpoonacularClientConfig {
    private String baseUrl;
    private String apiKey;
    private String source;
}