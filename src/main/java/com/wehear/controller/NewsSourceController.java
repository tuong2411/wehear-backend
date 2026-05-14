package com.wehear.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wehear.model.NewsSource;
import com.wehear.service.NewsSourceService;

@RestController
@RequestMapping("/api/news-sources")
public class NewsSourceController {

    private final NewsSourceService newsSourceService;

    public NewsSourceController(NewsSourceService newsSourceService) {
        this.newsSourceService = newsSourceService;
    }

    @GetMapping
    public ResponseEntity<List<NewsSource>> getAllActiveSources() {
        return ResponseEntity.ok(newsSourceService.getAllActiveSources());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsSource> getSourceById(@PathVariable Long id) {
        NewsSource source = newsSourceService.getSourceById(id);
        if (source == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(source);
    }
}
