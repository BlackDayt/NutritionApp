package io.nutritionApp;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
public class BackendApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(BackendApplication.class, args);
//    }
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(BackendApplication.class, args);

        Environment env = context.getEnvironment();
        System.out.println("✅ Активные профили: " + Arrays.toString(env.getActiveProfiles()));
    }

}
