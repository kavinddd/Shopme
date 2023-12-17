package com.shopme.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // allow spring project to be able to see user-photos directory and inside it
        String userPhotoDirectoryName = "user-photos";
        Path userPhotosDirectoryPath = Paths.get(userPhotoDirectoryName);
        System.out.println("userPhotosDirectoryPath: " + userPhotosDirectoryPath);
        String userPhotosDirectoryPathString = userPhotosDirectoryPath.toFile().getAbsolutePath();
        System.out.println("userPhotosDirectoryPathString: " +  userPhotosDirectoryPathString);

        registry.addResourceHandler("/" + userPhotoDirectoryName + "/**")
                .addResourceLocations("file:/" + userPhotosDirectoryPathString + "/");


        // allow spring project to be able to see category-images directory and inside it
        String categoryImageDirectoryName = "category-images";
        Path categoryImageDirectoryPath = Paths.get(categoryImageDirectoryName);
        System.out.println("categoryImageDirectoryPath: " + categoryImageDirectoryPath);
        String categoryImageDirectoryPathString = categoryImageDirectoryPath.toFile().getAbsolutePath();
        System.out.println("categoryImageDirectoryPathString: " + categoryImageDirectoryPathString);


        registry.addResourceHandler("/" + categoryImageDirectoryPath + "/**")
                .addResourceLocations("file:/" + categoryImageDirectoryPathString + "/");
    }

}
