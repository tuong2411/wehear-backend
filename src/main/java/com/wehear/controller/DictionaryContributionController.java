package com.wehear.controller;

import com.wehear.model.DictionaryContribution;
import com.wehear.model.User;
import com.wehear.repository.UserRepository;
import com.wehear.service.DictionaryContributionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contributions")
public class DictionaryContributionController {

    private final DictionaryContributionService contributionService;
    private final UserRepository userRepository;

    public DictionaryContributionController(DictionaryContributionService contributionService, UserRepository userRepository) {
        this.contributionService = contributionService;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @PostMapping
    public ResponseEntity<?> createContribution(@RequestBody DictionaryContribution contribution) {
        try {
            System.out.println("Received contribution request: " + contribution.getWord() + " (" + contribution.getType() + ")");
            contribution.setUserId(getCurrentUserId());
            Long id = contributionService.createContribution(contribution);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Contribution submitted successfully");
            response.put("id", id);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("Error creating contribution: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/upload-video")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            System.out.println("Uploading contribution video: " + file.getOriginalFilename());
            String videoUrl = contributionService.storeContributionVideo(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("videoUrl", videoUrl);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error uploading video: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyContributions() {
        List<DictionaryContribution> contributions = contributionService.getMyContributions(getCurrentUserId());
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", contributions);
        return ResponseEntity.ok(response);
    }

    // Admin Endpoints (though usually in an AdminController, I'll put them here or separate)
    // For consistency with existing code, let's keep them here but with role check in SecurityConfig
    
    @GetMapping("/admin/pending")
    public ResponseEntity<?> getPendingContributions() {
        List<DictionaryContribution> contributions = contributionService.getPendingContributions();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", contributions);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/{id}/approve")
    public ResponseEntity<?> approveContribution(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        try {
            String adminNote = body != null ? body.get("adminNote") : "Approved by admin";
            contributionService.approveContribution(id, adminNote);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Contribution approved and dictionary updated");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/admin/{id}/reject")
    public ResponseEntity<?> rejectContribution(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String adminNote = body != null ? body.get("adminNote") : "Rejected by admin";
            contributionService.rejectContribution(id, adminNote);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Contribution rejected");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
