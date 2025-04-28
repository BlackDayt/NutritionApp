package io.nutritionapp.datapipeline.controller;

import io.nutritionapp.datapipeline.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sheets/export")
@RequiredArgsConstructor
@Profile("dev") // Работает только в dev-профиле
public class GoogleSheetsExportController {

    private final ExportService exportService;

    @PostMapping("/all")
    public ResponseEntity<String> exportAllRecipes() {
        exportService.exportAllRecipesToGoogleSheet();
        return ResponseEntity.ok("Экспорт выполнен");
    }
}