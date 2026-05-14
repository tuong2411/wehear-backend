package com.wehear.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class SignDictionary {
    private Long id;
    private String labelCode;
    private String signWord;
    private String description;
    private String region;
    private String difficultyLevel;
    private String exampleSentence;
    @JsonProperty("isActive")
    private boolean isActive;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SignMedia> media;

    public SignDictionary() {}

    public SignDictionary(Long id, String labelCode, String signWord, String description, String region, String difficultyLevel, String exampleSentence, boolean isActive, Long createdBy, LocalDateTime createdAt, LocalDateTime updatedAt, List<SignMedia> media) {
        this.id = id;
        this.labelCode = labelCode;
        this.signWord = signWord;
        this.description = description;
        this.region = region;
        this.difficultyLevel = difficultyLevel;
        this.exampleSentence = exampleSentence;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.media = media;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLabelCode() { return labelCode; }
    public void setLabelCode(String labelCode) { this.labelCode = labelCode; }

    public String getSignWord() { return signWord; }
    public void setSignWord(String signWord) { this.signWord = signWord; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getExampleSentence() { return exampleSentence; }
    public void setExampleSentence(String exampleSentence) { this.exampleSentence = exampleSentence; }

    @JsonProperty("isActive")
    public boolean isActive() { return isActive; }
    
    @JsonProperty("isActive")
    public void setActive(boolean active) { isActive = active; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<SignMedia> getMedia() { return media; }
    public void setMedia(List<SignMedia> media) { this.media = media; }

    // Builder pattern (optional but useful)
    public static SignDictionaryBuilder builder() {
        return new SignDictionaryBuilder();
    }

    public static class SignDictionaryBuilder {
        private SignDictionary instance = new SignDictionary();

        public SignDictionaryBuilder id(Long id) { instance.setId(id); return this; }
        public SignDictionaryBuilder labelCode(String labelCode) { instance.setLabelCode(labelCode); return this; }
        public SignDictionaryBuilder signWord(String signWord) { instance.setSignWord(signWord); return this; }
        public SignDictionaryBuilder description(String description) { instance.setDescription(description); return this; }
        public SignDictionaryBuilder region(String region) { instance.setRegion(region); return this; }
        public SignDictionaryBuilder difficultyLevel(String difficultyLevel) { instance.setDifficultyLevel(difficultyLevel); return this; }
        public SignDictionaryBuilder exampleSentence(String exampleSentence) { instance.setExampleSentence(exampleSentence); return this; }
        public SignDictionaryBuilder isActive(boolean isActive) { instance.setActive(isActive); return this; }
        public SignDictionaryBuilder createdBy(Long createdBy) { instance.setCreatedBy(createdBy); return this; }
        public SignDictionaryBuilder createdAt(LocalDateTime createdAt) { instance.setCreatedAt(createdAt); return this; }
        public SignDictionaryBuilder updatedAt(LocalDateTime updatedAt) { instance.setUpdatedAt(updatedAt); return this; }
        public SignDictionaryBuilder media(List<SignMedia> media) { instance.setMedia(media); return this; }

        public SignDictionary build() { return instance; }
    }
}
