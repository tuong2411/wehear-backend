package com.wehear.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class SignMedia {
    private Long id;
    private Long signId;
    private String mediaType;
    private String mediaUrl;
    
    @JsonProperty("isPrimary")
    private boolean isPrimary;
    private LocalDateTime createdAt;

    public SignMedia() {}

    public SignMedia(Long id, Long signId, String mediaType, String mediaUrl, boolean isPrimary, LocalDateTime createdAt) {
        this.id = id;
        this.signId = signId;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
        this.isPrimary = isPrimary;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSignId() { return signId; }
    public void setSignId(Long signId) { this.signId = signId; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public String getMediaUrl() { 
        if (mediaUrl != null && !mediaUrl.startsWith("http")) {
            String backendUrl = System.getenv().getOrDefault("BACKEND_URL", "http://localhost:8668");
            return backendUrl + (mediaUrl.startsWith("/") ? "" : "/") + mediaUrl;
        }
        return mediaUrl; 
    }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static SignMediaBuilder builder() {
        return new SignMediaBuilder();
    }

    public static class SignMediaBuilder {
        private SignMedia instance = new SignMedia();

        public SignMediaBuilder id(Long id) { instance.setId(id); return this; }
        public SignMediaBuilder signId(Long signId) { instance.setSignId(signId); return this; }
        public SignMediaBuilder mediaType(String mediaType) { instance.setMediaType(mediaType); return this; }
        public SignMediaBuilder mediaUrl(String mediaUrl) { instance.setMediaUrl(mediaUrl); return this; }
        public SignMediaBuilder isPrimary(boolean isPrimary) { instance.setPrimary(isPrimary); return this; }
        public SignMediaBuilder createdAt(LocalDateTime createdAt) { instance.setCreatedAt(createdAt); return this; }

        public SignMedia build() { return instance; }
    }
}
