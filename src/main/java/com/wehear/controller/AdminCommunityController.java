package com.wehear.controller;

import com.wehear.service.CommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/community")
public class AdminCommunityController {

    private final CommunityService communityService;

    public AdminCommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts(@RequestParam(defaultValue = "0") int page, 
                                        @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", communityService.getAllPostsForAdmin(page, size));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/posts/{id}/hide")
    public ResponseEntity<?> hidePost(@PathVariable Long id) {
        communityService.hidePost(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã ẩn bài viết và tất cả bình luận liên quan");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/posts/{id}/show")
    public ResponseEntity<?> showPost(@PathVariable Long id) {
        communityService.showPost(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã hiện lại bài viết");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/comments/{id}/hide")
    public ResponseEntity<?> hideComment(@PathVariable Long id) {
        communityService.hideComment(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã ẩn bình luận");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/comments/{id}/show")
    public ResponseEntity<?> showComment(@PathVariable Long id) {
        communityService.showComment(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã hiện lại bình luận");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getReports() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", communityService.getReports());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reports/{id}/resolve")
    public ResponseEntity<?> resolveReport(@PathVariable Long id) {
        communityService.resolveReport(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã xử lý báo cáo");
        return ResponseEntity.ok(response);
    }
}
