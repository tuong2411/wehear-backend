package com.wehear.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.wehear.model.ExternalNewsArticle;

@Component
public class ExternalNewsArticleRowMapper implements RowMapper<ExternalNewsArticle> {

	@Override
	public ExternalNewsArticle mapRow(ResultSet rs, int rowNum) throws SQLException {
		ExternalNewsArticle article = new ExternalNewsArticle();
        article.setId(rs.getLong("id"));
        article.setSourceId(rs.getLong("source_id"));
        article.setExternalId(rs.getString("external_id"));
        article.setTitle(rs.getString("title"));
        article.setSlug(rs.getString("slug"));
        article.setSummary(rs.getString("summary"));
        article.setArticleUrl(rs.getString("article_url"));
        article.setThumbnailUrl(rs.getString("thumbnail_url"));
        
        Timestamp publishedAt = rs.getTimestamp("published_at");
        if (publishedAt != null) {
            article.setPublishedAt(publishedAt.toLocalDateTime());
        }
        
        Timestamp fetchedAt = rs.getTimestamp("fetched_at");
        if (fetchedAt != null) {
            article.setFetchedAt(fetchedAt.toLocalDateTime());
        }
        
        article.setAuthorName(rs.getString("author_name"));
        article.setCategory(rs.getString("category"));
        article.setTags(rs.getString("tags"));
        article.setLanguageCode(rs.getString("language_code"));
        article.setContentType(rs.getString("content_type"));
        article.setStatus(rs.getString("status"));
        article.setRelevanceScore(rs.getBigDecimal("relevance_score"));
        return article;
    }
}
