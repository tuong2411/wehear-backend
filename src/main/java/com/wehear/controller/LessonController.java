package com.wehear.controller;

import com.wehear.model.Lesson;
import com.wehear.service.LessonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping
    public ResponseEntity<List<Lesson>> getAllLessons() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    @GetMapping("/generate-ai")
    public ResponseEntity<Lesson> generateAILesson(@RequestParam String prompt) {
        return ResponseEntity.ok(lessonService.generateAILesson(prompt));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLessonById(@PathVariable Long id) {
        Lesson lesson = lessonService.getLessonById(id);
        if (lesson == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found");
        }
        return ResponseEntity.ok(lesson);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getLessonBySlug(@PathVariable String slug) {
        Lesson lesson = lessonService.getLessonBySlug(slug);
        if (lesson == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found");
        }
        return ResponseEntity.ok(lesson);
    }

    @PostMapping
    public ResponseEntity<?> createLesson(@RequestBody Lesson lesson) {
        try {
            Long id = lessonService.saveFullLesson(lesson, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create lesson: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLesson(@PathVariable Long id, @RequestBody Lesson lesson) {
        try {
            lesson.setId(id);
            lessonService.saveFullLesson(lesson, null);
            return ResponseEntity.ok("Lesson updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update lesson: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLesson(@PathVariable Long id) {
        boolean deleted = lessonService.deleteLesson(id);
        if (deleted) {
            return ResponseEntity.ok("Lesson deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete lesson");
    }
}
