package com.shopme.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private final Logger LOGGER = LoggerFactory.getLogger(MvcConfig.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // allow spring project to be able to see user-photos directory and inside it
        String userPhotoDirectoryName = "user-photos";
        Path userPhotosDirectoryPath = Paths.get(userPhotoDirectoryName);
        LOGGER.info("userPhotosDirectoryPath: {}", userPhotosDirectoryPath);
        String userPhotosDirectoryAbsPathString = userPhotosDirectoryPath.toFile().getAbsolutePath();
        LOGGER.info("userPhotosDirectoryAbsPathString: {}", userPhotosDirectoryAbsPathString);

        registry.addResourceHandler("/" + userPhotoDirectoryName + "/**")
                .addResourceLocations("file:/" + userPhotosDirectoryAbsPathString + "/");


        // allow spring project to be able to see category-images directory and inside it
        String categoryImageDirectoryName = "category-images";
        Path categoryImageDirectoryPath = Paths.get(categoryImageDirectoryName);
        LOGGER.info("categoryImageDirectoryPath: {}", categoryImageDirectoryPath);
        String categoryImageDirectoryAbsPathString = categoryImageDirectoryPath.toFile().getAbsolutePath();
        LOGGER.info("categoryImageDirectoryAbsPathString: {}", categoryImageDirectoryAbsPathString);


        registry.addResourceHandler("/" + categoryImageDirectoryPath + "/**")
                .addResourceLocations("file:/" + categoryImageDirectoryAbsPathString + "/");
    }

}
