package com.wehear.controller;

import com.wehear.model.User;
import com.wehear.repository.UserRepository;
import com.wehear.util.CloudinaryUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final CloudinaryUtil cloudinaryUtil;

    public UserController(UserRepository userRepository, CloudinaryUtil cloudinaryUtil) {
        this.userRepository = userRepository;
        this.cloudinaryUtil = cloudinaryUtil;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("Vui lòng đăng nhập");
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Long userId = getCurrentUserId();
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            // Clear password before sending to frontend
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User profileData) {
        try {
            Long userId = getCurrentUserId();
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setFullName(profileData.getFullName());
            user.setPhoneNumber(profileData.getPhoneNumber());
            user.setAvatarUrl(profileData.getAvatarUrl());
            
            userRepository.updateProfile(user);
            return ResponseEntity.ok("Cập nhật thông tin thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/profile/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String url = cloudinaryUtil.uploadByType(file, "avatar");
            Map<String, String> response = new HashMap<>();
            response.put("url", url);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Upload avatar failed: " + e.getMessage());
        }
    }
}
