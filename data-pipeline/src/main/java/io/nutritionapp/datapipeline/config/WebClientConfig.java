package io.nutritionapp.datapipeline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(SpoonacularClientConfig config) {
        return WebClient.builder()
                .baseUrl("https://" + config.getBaseUrl()) // добавляем https
                .build();
    }

    @Bean
    public WebClient googleSheetsWebClient() {
        return WebClient.builder()
                .baseUrl("https://sheets.googleapis.com/v4/spreadsheets")
                .build();
    }
}