package com.wehear.controller;

import com.wehear.dto.VslTranslationCorrectionRequest;
import com.wehear.model.User;
import com.wehear.model.VslTranslationCorrection;
import com.wehear.repository.UserRepository;
import com.wehear.service.VslTranslationCorrectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vsl-translation-corrections")
public class VslTranslationCorrectionController {

    private final VslTranslationCorrectionService correctionService;
    private final UserRepository userRepository;

    public VslTranslationCorrectionController(
            VslTranslationCorrectionService correctionService,
            UserRepository userRepository
    ) {
        this.correctionService = correctionService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> saveCorrection(@RequestBody VslTranslationCorrectionRequest request) {
        Long id = correctionService.saveCorrection(getCurrentUserId(), request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã lưu bản chỉnh sửa.");
        response.put("id", id);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyCorrections() {
        List<VslTranslationCorrection> data = correctionService.getMyCorrections(getCurrentUserId());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/training-data")
    public ResponseEntity<?> getRecentTrainingData(@RequestParam(defaultValue = "500") int limit) {
        List<VslTranslationCorrection> data = correctionService.getRecentTrainingData(limit);

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
