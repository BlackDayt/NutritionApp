package io.nutritionapp.datapipeline.client;

import io.nutritionapp.datapipeline.config.SpoonacularClientConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SpoonacularClient {

    private final WebClient webClient;
    private final SpoonacularClientConfig config;

    public SpoonacularClient(WebClient webClient, SpoonacularClientConfig config) {
        this.webClient = webClient;
        this.config = config;
    }

    public Mono<String> fetchRecipeRawData(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/complexSearch")
                        .queryParam("query", query)
                        .queryParam("apiKey", config.getApiKey())
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> fetchRecipeRawDataById(Long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/{id}/information")
                        .queryParam("includeNutrition", "true")
                        .queryParam("apiKey", config.getApiKey())
                        .build(id)
                )
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> fetchPopularRecipeIds(int count) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/complexSearch")
                        .queryParam("sort", "popularity")
                        .queryParam("number", count)
                        .queryParam("addRecipeInformation", "true")
                        .queryParam("includeNutrition", "true")
                        .queryParam("apiKey", config.getApiKey())
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> fetchPopularRecipeBatch(int number, int offset) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/complexSearch")
                        .queryParam("sort", "popularity")
                        .queryParam("number", number)
                        .queryParam("offset", offset)
                        .queryParam("addRecipeInformation", "true")
                        .queryParam("includeNutrition", "true")
                        .queryParam("apiKey", config.getApiKey())
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class);

    }
}


