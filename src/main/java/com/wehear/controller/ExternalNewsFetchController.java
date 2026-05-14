package com.wehear.controller;

import com.wehear.service.ExternalNewsFetchService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/external-news")
public class ExternalNewsFetchController {

    private final ExternalNewsFetchService fetchService;

    public ExternalNewsFetchController(ExternalNewsFetchService fetchService) {
        this.fetchService = fetchService;
    }

    @PostMapping("/fetch")
    public ResponseEntity<?> fetchNews() {
        int inserted = fetchService.fetchAllSources();
        return ResponseEntity.ok("Fetch completed. Inserted: " + inserted + " article(s)");
    }
}