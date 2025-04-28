package io.nutritionapp.datapipeline.controller;

import io.nutritionapp.datapipeline.config.SpoonacularClientConfig;
import io.nutritionapp.datapipeline.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@Profile("dev") // чтобы не работало в проде
public class ImportController {

    private final ImportService importService;
    private final SpoonacularClientConfig config;

    @PostMapping("/example")
    public ResponseEntity<String> importExampleRecipe() {
        String externalId = "715538"; // пример ID из Spoonacular (например, картошка)

        String result = importService.importRecipeFromApi(externalId, config.getSource());

        return ResponseEntity.ok("Импортировано: " + result);
    }

    @PostMapping("/popular")
    public ResponseEntity<String> importPopularRecipes(@RequestParam(name = "count", defaultValue = "100") int count) {
        importService.importPopularRecipes(count);
        return ResponseEntity.ok("Импортировано популярных рецептов: " + count);
    }
}