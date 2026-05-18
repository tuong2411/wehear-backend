package com.wehear.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${FRONTEND_URL:http://localhost:3001}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> origins = new ArrayList<>(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://localhost:5173",
                "https://www.wehear.today",
                "https://wehear.today",
                "https://wehear-frontend.tuongporo9x2004.workers.dev",
                "https://wehear-frontend.pages.dev"
        ));
        if (frontendUrl != null && !frontendUrl.isEmpty()) {
            origins.add(frontendUrl);
        }
        String[] allowedOrigins = origins.toArray(new String[0]);

        // Cho phép Frontend truy cập API
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        registry.addMapping("/uploads/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /media/dataset/** tới thư mục vật lý Dataset/Videos/
        // Đã chuyển sang Cloudinary, comment lại để không dùng local nữa
        /*
        String projectRoot = System.getProperty("user.dir");
        File rootDir = new File(projectRoot);
        
        if (rootDir.getName().equals("wehear-backend")) {
            rootDir = rootDir.getParentFile().getParentFile();
        }
        
        String absolutePath = rootDir.getAbsolutePath().replace("\\", "/");
        if (!absolutePath.endsWith("/")) {
            absolutePath += "/";
        }
        
        String videoPath = "file:/" + absolutePath + "Dataset/Videos/";
        
        registry.addResourceHandler("/media/dataset/**")
                .addResourceLocations(videoPath)
                .setCachePeriod(3600)
                .resourceChain(true);
        System.out.println("Static resource mapped: /media/dataset/** -> " + videoPath);
        */

        // Path to uploads/
        String projectRoot = System.getProperty("user.dir");
        File rootDir = new File(projectRoot);
        if (rootDir.getName().equals("wehear-backend")) {
            rootDir = rootDir.getParentFile().getParentFile();
        }
        String absolutePath = rootDir.getAbsolutePath().replace("\\", "/");
        if (!absolutePath.endsWith("/")) {
            absolutePath += "/";
        }

        String uploadsPath;
        File uploadsDir = new File(System.getProperty("user.dir"), "uploads");
        if (uploadsDir.exists()) {
            uploadsPath = "file:/" + uploadsDir.getAbsolutePath().replace("\\", "/") + "/";
        } else {
            uploadsPath = "file:/" + absolutePath + "backend/wehear-backend/uploads/";
        }
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsPath)
                .setCachePeriod(3600)
                .resourceChain(true);

        registry.addResourceHandler("/videos/**")
                .addResourceLocations(uploadsPath)
                .setCachePeriod(3600)
                .resourceChain(true);
                
        System.out.println("Static resource mapped: /uploads/** -> " + uploadsPath);
        System.out.println("Static resource mapped: /videos/** -> " + uploadsPath);
    }
}
