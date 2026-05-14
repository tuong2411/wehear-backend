package com.wehear.model;

import java.time.LocalDateTime;

public class NewsSource {
    private Long id;
    private String sourceName;
    private String sourceType;
    private String baseUrl;
    private String rssUrl;
    private String apiUrl;
    private boolean isActive;
    private LocalDateTime createdAt;

    public NewsSource() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getRssUrl() { return rssUrl; }
    public void setRssUrl(String rssUrl) { this.rssUrl = rssUrl; }

    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static NewsSourceBuilder builder() {
        return new NewsSourceBuilder();
    }

    public static class NewsSourceBuilder {
        private NewsSource instance = new NewsSource();

        public NewsSourceBuilder id(Long id) { instance.setId(id); return this; }
        public NewsSourceBuilder sourceName(String sourceName) { instance.setSourceName(sourceName); return this; }
        public NewsSourceBuilder sourceType(String sourceType) { instance.setSourceType(sourceType); return this; }
        public NewsSourceBuilder baseUrl(String baseUrl) { instance.setBaseUrl(baseUrl); return this; }
        public NewsSourceBuilder rssUrl(String rssUrl) { instance.setRssUrl(rssUrl); return this; }
        public NewsSourceBuilder apiUrl(String apiUrl) { instance.setApiUrl(apiUrl); return this; }
        public NewsSourceBuilder isActive(boolean isActive) { instance.setActive(isActive); return this; }
        public NewsSourceBuilder createdAt(LocalDateTime createdAt) { instance.setCreatedAt(createdAt); return this; }

        public NewsSource build() { return instance; }
    }
}
