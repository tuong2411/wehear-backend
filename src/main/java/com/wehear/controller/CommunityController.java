package com.wehear.controller;

import com.wehear.model.CommunityComment;
import com.wehear.model.CommunityPost;
import com.wehear.model.CommunityReport;
import com.wehear.model.User;
import com.wehear.repository.UserRepository;
import com.wehear.service.CommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;
    private final UserRepository userRepository;
    private final com.wehear.util.CloudinaryUtil cloudinaryUtil;

    public CommunityController(CommunityService communityService, UserRepository userRepository, com.wehear.util.CloudinaryUtil cloudinaryUtil) {
        this.communityService = communityService;
        this.userRepository = userRepository;
        this.cloudinaryUtil = cloudinaryUtil;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || "anonymousUser".equals(authentication.getName())) return null;
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody CommunityPost post) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) throw new RuntimeException("Vui lòng đăng nhập để đăng bài");
            post.setUserId(userId);
            Long id = communityService.createPost(post);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng bài thành công");
            response.put("id", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue = "0") int page, 
                                     @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", communityService.getActivePosts(page, size, userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            CommunityPost post = communityService.getPostDetail(id, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", post);
            response.put("comments", communityService.getCommentsForPost(id, userId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/comments")
    public ResponseEntity<?> addComment(@RequestBody CommunityComment comment) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) throw new RuntimeException("Vui lòng đăng nhập để bình luận");
            comment.setUserId(userId);
            communityService.addComment(comment);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bình luận thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/like")
    public ResponseEntity<?> toggleLike(@RequestBody Map<String, Long> request) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) throw new RuntimeException("Vui lòng đăng nhập để like");
            
            boolean isLiked = false;
            if (request.containsKey("postId")) {
                isLiked = communityService.togglePostLike(userId, request.get("postId"));
            } else if (request.containsKey("commentId")) {
                isLiked = communityService.toggleCommentLike(userId, request.get("commentId"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("liked", isLiked);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/report")
    public ResponseEntity<?> report(@RequestBody CommunityReport report) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) throw new RuntimeException("Vui lòng đăng nhập để báo cáo");
            report.setReporterId(userId);
            communityService.reportContent(report);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã gửi báo cáo");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/upload-media")
    public ResponseEntity<?> uploadMedia(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            String fileUrl = cloudinaryUtil.uploadByType(file, "community");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("url", fileUrl);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Upload failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
