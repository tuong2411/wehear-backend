package com.wehear.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExternalNewsArticle {
    private Long id;
    private Long sourceId;
    private String externalId;
    private String title;
    private String slug;
    private String summary;
    private String articleUrl;
    private String thumbnailUrl;
    private LocalDateTime publishedAt;
    private LocalDateTime fetchedAt;
    private String authorName;
    private String category;
    private String tags;
    private String languageCode;
    private String contentType;
    private String status;
    private BigDecimal relevanceScore;

    public ExternalNewsArticle() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getArticleUrl() { return articleUrl; }
    public void setArticleUrl(String articleUrl) { this.articleUrl = articleUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public LocalDateTime getFetchedAt() { return fetchedAt; }
    public void setFetchedAt(LocalDateTime fetchedAt) { this.fetchedAt = fetchedAt; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(BigDecimal relevanceScore) { this.relevanceScore = relevanceScore; }

    public static ExternalNewsArticleBuilder builder() {
        return new ExternalNewsArticleBuilder();
    }

    public static class ExternalNewsArticleBuilder {
        private ExternalNewsArticle instance = new ExternalNewsArticle();

        public ExternalNewsArticleBuilder id(Long id) { instance.setId(id); return this; }
        public ExternalNewsArticleBuilder sourceId(Long sourceId) { instance.setSourceId(sourceId); return this; }
        public ExternalNewsArticleBuilder externalId(String externalId) { instance.setExternalId(externalId); return this; }
        public ExternalNewsArticleBuilder title(String title) { instance.setTitle(title); return this; }
        public ExternalNewsArticleBuilder slug(String slug) { instance.setSlug(slug); return this; }
        public ExternalNewsArticleBuilder summary(String summary) { instance.setSummary(summary); return this; }
        public ExternalNewsArticleBuilder articleUrl(String articleUrl) { instance.setArticleUrl(articleUrl); return this; }
        public ExternalNewsArticleBuilder thumbnailUrl(String thumbnailUrl) { instance.setThumbnailUrl(thumbnailUrl); return this; }
        public ExternalNewsArticleBuilder publishedAt(LocalDateTime publishedAt) { instance.setPublishedAt(publishedAt); return this; }
        public ExternalNewsArticleBuilder fetchedAt(LocalDateTime fetchedAt) { instance.setFetchedAt(fetchedAt); return this; }
        public ExternalNewsArticleBuilder authorName(String authorName) { instance.setAuthorName(authorName); return this; }
        public ExternalNewsArticleBuilder category(String category) { instance.setCategory(category); return this; }
        public ExternalNewsArticleBuilder tags(String tags) { instance.setTags(tags); return this; }
        public ExternalNewsArticleBuilder languageCode(String languageCode) { instance.setLanguageCode(languageCode); return this; }
        public ExternalNewsArticleBuilder contentType(String contentType) { instance.setContentType(contentType); return this; }
        public ExternalNewsArticleBuilder status(String status) { instance.setStatus(status); return this; }
        public ExternalNewsArticleBuilder relevanceScore(BigDecimal relevanceScore) { instance.setRelevanceScore(relevanceScore); return this; }

        public ExternalNewsArticle build() { return instance; }
    }
}
