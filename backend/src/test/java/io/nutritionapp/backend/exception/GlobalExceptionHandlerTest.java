package io.nutritionapp.backend.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test-postgres")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // Создаем фиктивный тестовый контроллер для генерации ошибки
    @RestController
    static class TestController {
        @GetMapping("/test-exception")
        public void triggerGenericException() {
            throw new RuntimeException("Что-то пошло не так");
        }
    }

    @Test
    void shouldHandleGenericException() throws Exception {
        mockMvc.perform(get("/test-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Произошла непредвиденная ошибка. Обратитесь к администратору."));
    }
}

