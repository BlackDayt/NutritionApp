package io.nutritionApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // Отключаем CSRF (не нужен для H2 Console)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()  // Разрешаем доступ к H2 Console
                        .requestMatchers("/**").permitAll()  // Разрешаем API
                        .anyRequest().authenticated()   // Остальные запросы требуют аутентификации
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())) // Разрешаем iframes только с того же источника (localhost)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));  // Отключаем сессии

        return http.build();
    }

}
