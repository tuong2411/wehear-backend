package com.wehear.controller;

import com.wehear.util.CloudinaryUtil;
import com.wehear.model.User;
import com.wehear.model.VslUploadVideo;
import com.wehear.repository.UserRepository;
import com.wehear.service.VslUploadVideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vsl-upload-videos")
public class VslUploadVideoController {

    private static final long MAX_VIDEO_SIZE_BYTES = 50L * 1024L * 1024L;
    private static final String VSL_UPLOAD_FOLDER = "wehear/vsl-uploads";

    private final CloudinaryUtil cloudinaryUtil;
    private final VslUploadVideoService uploadVideoService;
    private final UserRepository userRepository;

    public VslUploadVideoController(
            CloudinaryUtil cloudinaryUtil,
            VslUploadVideoService uploadVideoService,
            UserRepository userRepository
    ) {
        this.cloudinaryUtil = cloudinaryUtil;
        this.uploadVideoService = uploadVideoService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> uploadVslVideo(
            @RequestParam("video") MultipartFile video,
            @RequestParam("selectedLabel") String selectedLabel,
            @RequestParam(value = "confidence", required = false) Double confidence
    ) {
        if (video == null || video.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Video không được để trống."));
        }

        String contentType = video.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng tải lên tệp video."));
        }

        if (video.getSize() > MAX_VIDEO_SIZE_BYTES) {
            return ResponseEntity.badRequest().body(Map.of("message", "Video không được vượt quá 50MB."));
        }

        String normalizedLabel = selectedLabel == null ? "" : selectedLabel.trim().replaceAll("\\s+", " ");
        if (normalizedLabel.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng chọn từ nhận diện trước khi lưu."));
        }

        try {
            String videoUrl = cloudinaryUtil.upload(video, VSL_UPLOAD_FOLDER);
            Long id = uploadVideoService.saveUpload(getCurrentUserId(), videoUrl, normalizedLabel, confidence);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã lưu video lên Cloudinary.");
            response.put("id", id);
            response.put("videoUrl", videoUrl);
            response.put("selectedLabel", normalizedLabel);
            response.put("confidence", confidence);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Không thể lưu video lên Cloudinary."));
        }
    }

    @GetMapping("/training-data")
    public ResponseEntity<?> getRecentUploads(@RequestParam(defaultValue = "500") int limit) {
        List<VslUploadVideo> data = uploadVideoService.getRecentUploads(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}
