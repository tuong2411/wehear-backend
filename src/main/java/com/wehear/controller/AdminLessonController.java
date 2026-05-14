package com.wehear.controller;

import com.wehear.dto.LessonRequest;
import com.wehear.dto.LessonSuggestionRequest;
import com.wehear.model.Lesson;
import com.wehear.model.SignDictionary;
import com.wehear.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.wehear.model.QuizQuestion;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin/lessons")
public class AdminLessonController {

    private final LessonService lessonService;

    public AdminLessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping("/generate-quiz")
    public ResponseEntity<List<QuizQuestion>> generateAIQuiz(@RequestBody List<String> signWords) {
        if (signWords == null || signWords.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(lessonService.generateQuizFromSigns(signWords));
    }

    @PostMapping("/suggest-signs")
    public ResponseEntity<List<SignDictionary>> suggestSigns(@RequestBody LessonSuggestionRequest request) {
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(lessonService.suggestSignsForLesson(request.getTitle(), request.getDescription()));
    }

    @GetMapping
    public ResponseEntity<List<Lesson>> getAllLessons() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Long id) {
        Lesson lesson = lessonService.getLessonById(id);
        if (lesson != null) {
            return ResponseEntity.ok(lesson);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createLesson(@RequestBody LessonRequest request) {
        Lesson lesson = mapRequestToLesson(request);
        Long id = lessonService.saveFullLesson(lesson, request.getSignIds());
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLesson(@PathVariable Long id, @RequestBody LessonRequest request) {
        request.setId(id);
        Lesson lesson = mapRequestToLesson(request);
        lessonService.saveFullLesson(lesson, request.getSignIds());
        return ResponseEntity.ok("Cập nhật bài học thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long id) {
        if (lessonService.deleteLesson(id)) {
            return ResponseEntity.ok("Xóa bài học thành công");
        }
        return ResponseEntity.badRequest().body("Xóa bài học thất bại");
    }

    private Lesson mapRequestToLesson(LessonRequest request) {
        Lesson lesson = new Lesson();
        lesson.setId(request.getId());
        lesson.setTopicId(request.getTopicId());
        lesson.setTitle(request.getTitle());
        lesson.setSlug(request.getSlug());
        lesson.setDescription(request.getDescription());
        lesson.setCoverImage(request.getCoverImage());
        lesson.setLevel(request.getLevel());
        lesson.setRegion(request.getRegion());
        lesson.setStatus(request.getStatus());
        lesson.setFeatured(request.isFeatured());
        lesson.setPublishAt(request.getPublishAt());
        lesson.setUnpublishAt(request.getUnpublishAt());
        lesson.setQuiz(request.getQuiz());
        return lesson;
    }
}
