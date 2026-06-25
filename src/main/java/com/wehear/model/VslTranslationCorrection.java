package com.wehear.model;

import java.time.LocalDateTime;

public class VslTranslationCorrection {
    private Long id;
    private Long userId;
    private String sourceText;
    private String modelName;
    private String modelTranslation;
    private String correctedTranslation;
    private String username;
    private String userFullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelTranslation() {
        return modelTranslation;
    }

    public void setModelTranslation(String modelTranslation) {
        this.modelTranslation = modelTranslation;
    }

    public String getCorrectedTranslation() {
        return correctedTranslation;
    }

    public void setCorrectedTranslation(String correctedTranslation) {
        this.correctedTranslation = correctedTranslation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
