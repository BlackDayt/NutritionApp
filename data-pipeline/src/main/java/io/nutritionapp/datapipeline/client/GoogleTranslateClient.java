package io.nutritionapp.datapipeline.client;

import org.springframework.stereotype.Component;

@Component
public class GoogleTranslateClient {
    public String translate(String original) {
        // TODO: Подключить реальный Google Translate API
        return "[переведено] " + original;
    }
}
