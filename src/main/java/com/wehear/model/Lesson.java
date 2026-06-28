package com.wehear.model;

import java.time.LocalDateTime;
import java.util.List;

public class Lesson {
    private Long id;
    private Long topicId;
    private String title;
    private String slug;
    private String description;
    private String coverImage;
    private LessonLevel level;
    private LessonRegion region;
    private LessonStatus status;
    private boolean isFeatured;
    private LocalDateTime publishAt;
    private LocalDateTime unpublishAt;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer signCount;

    // Danh sách từ vựng trong bài học (chứa thông tin thứ tự từ lesson_items)
    private List<SignDictionary> signs;
    
    // Quiz đi kèm (nếu có)
    private Quiz quiz;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public LessonLevel getLevel() { return level; }
    public void setLevel(LessonLevel level) { this.level = level; }
    public LessonRegion getRegion() { return region; }
    public void setRegion(LessonRegion region) { this.region = region; }
    public LessonStatus getStatus() { return status; }
    public void setStatus(LessonStatus status) { this.status = status; }
    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }
    public LocalDateTime getPublishAt() { return publishAt; }
    public void setPublishAt(LocalDateTime publishAt) { this.publishAt = publishAt; }
    public LocalDateTime getUnpublishAt() { return unpublishAt; }
    public void setUnpublishAt(LocalDateTime unpublishAt) { this.unpublishAt = unpublishAt; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getSignCount() { return signCount; }
    public void setSignCount(Integer signCount) { this.signCount = signCount; }
    public List<SignDictionary> getSigns() { return signs; }
    public void setSigns(List<SignDictionary> signs) { this.signs = signs; }
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
}
