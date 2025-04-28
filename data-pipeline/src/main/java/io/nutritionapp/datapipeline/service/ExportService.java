package io.nutritionapp.datapipeline.service;

import com.google.common.net.HttpHeaders;
import io.nutritionapp.datapipeline.dto.RecipeDto;
import io.nutritionapp.datapipeline.external.GoogleAccessTokenProvider;
import io.nutritionapp.datapipeline.model.entity.ApiRecipe;
import io.nutritionapp.datapipeline.repository.ApiRecipeRepository;
import io.nutritionapp.datapipeline.util.ApiRecipeToRecipeDtoMapper;
import io.nutritionapp.datapipeline.util.RecipeDtoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {
    private final WebClient googleSheetsWebClient;
    private final ApiRecipeRepository apiRecipeRepository;
    private final ApiRecipeToRecipeDtoMapper mapper;
    private final GoogleAccessTokenProvider tokenProvider;
    private final TranslationService translationService;
    private final RecipeDtoValidator recipeDtoValidator;

    @Value("${google.sheets.spreadsheet-id}")
    private String spreadsheetId;

    @Value("${google.sheets.export-sheet-name}")
    private String sheetName;

    public void exportAllRecipesToGoogleSheet() {
        List<ApiRecipe> recipes = apiRecipeRepository.findAll();

        if (recipes.isEmpty()) {
            log.warn("Нет рецептов для экспорта.");
            return;
        }

        long publicIdCounter = 1L;
        List<List<Object>> rows = new ArrayList<>();

        for (ApiRecipe recipe : recipes) {
            try {

                RecipeDto dto = translationService.translateRecipe(recipe);
                dto.setPublicId(publicIdCounter++);


                // Валидация
                List<String> errors = recipeDtoValidator.validateAndReport(dto);
                if (!errors.isEmpty()) {
                    log.warn("❌ Рецепт {} не прошёл валидацию: {}", dto.getExternalId(), errors);
                    continue;
                }

                rows.add(toRow(dto));

            } catch (Exception e) {
                log.error("Ошибка при обработке рецепта ID={}: {}", recipe.getId(), e.getMessage(), e);
            }
        }

        if (!rows.isEmpty()) {
            sendToGoogleSheet(rows);
            log.info("Экспорт завершён: {} рецептов отправлено в Google Таблицу", rows.size());
        } else {
            log.warn("Ни один рецепт не прошёл проверку, экспорт не выполнен");
        }
    }

//    private void sendToGoogleSheet(List<List<Object>> rows) {
//        String accessToken = tokenProvider.getAccessToken();
//
//        WebClient googleClient = webClient.mutate()
//                .baseUrl("https://sheets.googleapis.com/v4/spreadsheets")
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                .build();
//
//        String range = sheetName + "!A1"; // можно сделать параметром
//
//        Map<String, Object> requestBody = Map.of("values", rows);
//
//        googleClient.post()
//                .uri("/{spreadsheetId}/values/{range}:append?valueInputOption=USER_ENTERED", spreadsheetId, range)
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .doOnError(e -> log.error("Ошибка при экспорте в Google Таблицу", e))
//                .block();
//    }

    private void sendToGoogleSheet(List<List<Object>> rows) {
        String accessToken = tokenProvider.getAccessToken();

        Map<String, Object> requestBody = Map.of("values", rows);
        String range = sheetName + "!A1"; // можно сделать параметром

        googleSheetsWebClient.post()
                .uri("/{spreadsheetId}/values/{range}:append?valueInputOption=USER_ENTERED", spreadsheetId, range)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                .headers(headers -> headers.setBearerAuth(accessToken))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("Ошибка при экспорте в Google Таблицу", e))
                .block();
    }


//    private void sendToGoogleSheet(List<List<Object>> rows) {
//        String accessToken = tokenProvider.getAccessToken();
//        String range = sheetName + "!A1";
//
//        Map<String, Object> requestBody = Map.of("values", rows);
//
//        webClient.post()
//                .uri("/{spreadsheetId}/values/{range}:append?valueInputOption=USER_ENTERED", spreadsheetId, range)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .doOnError(e -> log.error("Ошибка при экспорте в Google Таблицу", e))
//                .block();
//    }

    private List<Object> toRow(RecipeDto dto) {
        return Arrays.asList(
                dto.getPublicId(),
                dto.getExternalId(),
                dto.getName(),
                dto.getDescription(),
                String.join(", ", dto.getTags()),
                dto.getCalories(),
                dto.getProteins(),
                dto.getFats(),
                dto.getCarbohydrates(),
                dto.getCookTime(),
                dto.getServings(),
                String.join(", ", dto.getIngredients()),
                dto.getInstructions(),
                dto.getImageUrl()
        );
    }

}
