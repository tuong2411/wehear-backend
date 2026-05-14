package com.wehear.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wehear.model.ExternalNewsArticle;
import com.wehear.service.ExternalNewsService;

@RestController
@RequestMapping("/api/external-news")
public class ExternalNewsController {

	private final ExternalNewsService externalNewsService;

    public ExternalNewsController(ExternalNewsService externalNewsService) {
        this.externalNewsService = externalNewsService;
    }

    @GetMapping
    public ResponseEntity<List<ExternalNewsArticle>> getAllActiveNews() {
        return ResponseEntity.ok(externalNewsService.getAllActiveNews());
    }

    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> getAllNews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<ExternalNewsArticle> news = externalNewsService.getPagedNews(page, size);
        int totalItems = externalNewsService.getTotalCount();
        
        Map<String, Object> response = new HashMap<>();
        response.put("news", news);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", (int) Math.ceil((double) totalItems / size));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getNewsBySlug(@PathVariable String slug) {
        ExternalNewsArticle article = externalNewsService.getNewsBySlug(slug);

        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("External news not found with slug = " + slug);
        }

        return ResponseEntity.ok(article);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getNewsById(@PathVariable Long id) {
        ExternalNewsArticle article = externalNewsService.getNewsById(id);

        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("External news not found with id = " + id);
        }

        return ResponseEntity.ok(article);
    }

    @GetMapping("/source/{sourceId}")
    public ResponseEntity<List<ExternalNewsArticle>> getNewsBySource(@PathVariable Long sourceId) {
        return ResponseEntity.ok(externalNewsService.getNewsBySourceId(sourceId));
    }

    @PostMapping
    public ResponseEntity<String> createNews(@RequestBody ExternalNewsArticle article) {
        externalNewsService.createNews(article);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created news article successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateNews(@PathVariable Long id, @RequestBody ExternalNewsArticle article) {
        article.setId(id);
        externalNewsService.updateNews(article);
        return ResponseEntity.ok("Updated news article successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNews(@PathVariable Long id) {
        externalNewsService.deleteNews(id);
        return ResponseEntity.ok("Deleted news article successfully");
    }
}
