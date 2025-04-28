package io.nutritionapp.datapipeline.external;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class GoogleAccessTokenProvider {

    private final GoogleCredentials credentials;

    public GoogleAccessTokenProvider() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("credentials.json")) {
            if (stream == null) {
                throw new IllegalStateException("credentials.json not found");
            }

            this.credentials = GoogleCredentials.fromStream(stream)
                    .createScoped("https://www.googleapis.com/auth/spreadsheets");
            this.credentials.refreshIfExpired();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Google credentials", e);
        }
    }

    public String getAccessToken() {
        try {
            credentials.refreshIfExpired();
            return credentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new RuntimeException("Unable to refresh Google access token", e);
        }
    }
}