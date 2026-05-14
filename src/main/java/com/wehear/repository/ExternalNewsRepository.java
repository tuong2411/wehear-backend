package com.wehear.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.wehear.model.ExternalNewsArticle;
import com.wehear.repository.mapper.ExternalNewsArticleRowMapper;

@Repository
public class ExternalNewsRepository {

	private final JdbcTemplate jdbcTemplate;
    private final ExternalNewsArticleRowMapper rowMapper;

    public ExternalNewsRepository(JdbcTemplate jdbcTemplate, ExternalNewsArticleRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }
    public List<ExternalNewsArticle> findAllActive() {
        String sql = """
                SELECT *
                FROM external_news_articles
                WHERE status = 'ACTIVE'
                ORDER BY published_at DESC, id DESC
                """;
        return jdbcTemplate.query(sql, rowMapper);
    }
    public List<ExternalNewsArticle> findAll() {
        String sql = """
                SELECT *
                FROM external_news_articles
                ORDER BY published_at DESC, id DESC
                """;
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<ExternalNewsArticle> findAllPaged(int limit, int offset) {
        String sql = """
                SELECT *
                FROM external_news_articles
                ORDER BY published_at DESC, id DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, rowMapper, limit, offset);
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM external_news_articles";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public ExternalNewsArticle findBySlug(String slug) {
        String sql = """
                SELECT *
                FROM external_news_articles
                WHERE slug = ? AND status = 'ACTIVE'
                LIMIT 1
                """;

        List<ExternalNewsArticle> result = jdbcTemplate.query(sql, rowMapper, slug);
        return result.isEmpty() ? null : result.get(0);
    }
    public List<ExternalNewsArticle> findBySourceId(Long sourceId) {
        String sql = """
                SELECT *
                FROM external_news_articles
                WHERE source_id = ? AND status = 'ACTIVE'
                ORDER BY published_at DESC, id DESC
                """;
        return jdbcTemplate.query(sql, rowMapper, sourceId);
    }

    public ExternalNewsArticle findById(Long id) {
        String sql = """
                SELECT *
                FROM external_news_articles
                WHERE id = ?
                """;
        List<ExternalNewsArticle> result = jdbcTemplate.query(sql, rowMapper, id);
        return result.isEmpty() ? null : result.get(0);
    }
    
    public boolean existsByArticleUrl(String articleUrl) {
        String sql = """
                SELECT COUNT(*)
                FROM external_news_articles
                WHERE article_url = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, articleUrl);
        return count != null && count > 0;
    }
    
    public int insert(ExternalNewsArticle article) {
        String sql = """
                INSERT INTO external_news_articles
                (
                    source_id, external_id, title, slug, summary, article_url,
                    thumbnail_url, published_at, author_name, category, tags,
                    language_code, content_type, status, relevance_score
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                article.getSourceId(),
                article.getExternalId(),
                article.getTitle(),
                article.getSlug(),
                article.getSummary(),
                article.getArticleUrl(),
                article.getThumbnailUrl(),
                article.getPublishedAt(),
                article.getAuthorName(),
                article.getCategory(),
                article.getTags(),
                article.getLanguageCode(),
                article.getContentType(),
                article.getStatus(),
                article.getRelevanceScore()
        );
    }

    public int update(ExternalNewsArticle article) {
        String sql = """
                UPDATE external_news_articles
                SET source_id = ?, external_id = ?, title = ?, slug = ?, summary = ?,
                    article_url = ?, thumbnail_url = ?, published_at = ?, author_name = ?,
                    category = ?, tags = ?, language_code = ?, content_type = ?,
                    status = ?, relevance_score = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                article.getSourceId(),
                article.getExternalId(),
                article.getTitle(),
                article.getSlug(),
                article.getSummary(),
                article.getArticleUrl(),
                article.getThumbnailUrl(),
                article.getPublishedAt(),
                article.getAuthorName(),
                article.getCategory(),
                article.getTags(),
                article.getLanguageCode(),
                article.getContentType(),
                article.getStatus(),
                article.getRelevanceScore(),
                article.getId()
        );
    }

    public int delete(Long id) {
        String sql = "DELETE FROM external_news_articles WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
