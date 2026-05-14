package com.wehear.controller;

import com.wehear.dto.BulkActionRequest;
import com.wehear.model.SignDictionary;
import com.wehear.service.DictionaryImportService;
import com.wehear.service.DictionaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dictionary")
public class DictionaryController {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryController.class);
    private final DictionaryService dictionaryService;
    private final DictionaryImportService importService;

    public DictionaryController(DictionaryService dictionaryService, DictionaryImportService importService) {
        this.dictionaryService = dictionaryService;
        this.importService = importService;
    }

    @PostMapping("/import")
    public ResponseEntity<?> importDataset() {
        try {
            String csvPath = "../../Dataset/Labels/label.csv"; 
            Map<String, Object> result = importService.importFromCsv(csvPath);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Import failed: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "40") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String region) {
        
        logger.info("Request getAllSigns: page={}, size={}, search={}, region={}", page, size, search, region);
        
        List<SignDictionary> signs = dictionaryService.getPaginatedSigns(page, size, search, region);
        int totalItems = dictionaryService.getTotalCount(search, region);
        int totalPages = (int) Math.ceil((double) totalItems / size);

        Map<String, Object> response = new HashMap<>();
        response.put("items", signs);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        response.put("pageSize", size);

        logger.info("Response getAllSigns: found {} items", totalItems);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSignById(@PathVariable Long id) {
        SignDictionary sign = dictionaryService.getSignById(id);
        if (sign == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Sign not found with id = " + id);
        }
        return ResponseEntity.ok(sign);
    }

    @PostMapping
    public ResponseEntity<?> createSign(@RequestBody SignDictionary sign) {
        Long id = dictionaryService.createSign(sign);
        if (id != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Create sign failed");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSign(@PathVariable Long id, @RequestBody SignDictionary sign) {
        sign.setId(id);
        boolean updated = dictionaryService.updateSign(sign);
        if (updated) {
            return ResponseEntity.ok("Update sign successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update sign failed");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> toggleStatus(@PathVariable Long id, @RequestParam boolean active) {
        boolean updated = dictionaryService.updateStatus(id, active);
        if (updated) {
            return ResponseEntity.ok("Update status successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update status failed");
    }

    @PostMapping("/bulk-action")
    public ResponseEntity<String> bulkAction(@RequestBody BulkActionRequest request) {
        try {
            dictionaryService.bulkAction(request.getIds(), request.getAction());
            return ResponseEntity.ok("Bulk action " + request.getAction() + " executed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Bulk action failed: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/upload-media")
    public ResponseEntity<?> uploadMedia(
            @PathVariable Long id, 
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(defaultValue = "VIDEO") String type) {
        try {
            String mediaUrl = dictionaryService.storeMedia(file);
            dictionaryService.addMedia(id, mediaUrl, type, true);
            return ResponseEntity.ok(mediaUrl);
        } catch (java.io.IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSign(@PathVariable Long id) {
        boolean deleted = dictionaryService.deleteSign(id);
        if (deleted) {
            return ResponseEntity.ok("Delete sign successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delete sign failed");
    }
}
