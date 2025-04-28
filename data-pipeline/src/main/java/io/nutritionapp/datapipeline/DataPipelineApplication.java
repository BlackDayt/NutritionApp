package io.nutritionapp.datapipeline;

import io.nutritionapp.datapipeline.config.SpoonacularClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

//@EnableConfigurationProperties(SpoonacularClientConfig.class)
@SpringBootApplication
public class DataPipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataPipelineApplication.class, args);
    }

}
