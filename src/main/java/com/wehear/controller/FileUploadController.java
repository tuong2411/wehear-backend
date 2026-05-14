package com.wehear.controller;

import com.wehear.util.CloudinaryUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/upload")
public class FileUploadController {

    private final CloudinaryUtil cloudinaryUtil;

    public FileUploadController(CloudinaryUtil cloudinaryUtil) {
        this.cloudinaryUtil = cloudinaryUtil;
    }

    @PostMapping("/lesson-cover")
    public ResponseEntity<?> uploadLessonCover(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            String fileUrl = cloudinaryUtil.uploadByType(file, "lesson");
            
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Could not upload to Cloudinary: " + e.getMessage());
        }
    }
}
