package com.wehear.repository;

import com.wehear.model.Lesson;
import com.wehear.repository.mapper.LessonRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class LessonRepository {

    private final JdbcTemplate jdbcTemplate;
    private final LessonRowMapper lessonRowMapper;

    public LessonRepository(JdbcTemplate jdbcTemplate, LessonRowMapper lessonRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.lessonRowMapper = lessonRowMapper;
    }

    public List<Lesson> findAll() {
        String sql = "SELECT l.*, COALESCE(item_counts.sign_count, 0) AS sign_count " +
                "FROM lessons l " +
                "LEFT JOIN (" +
                "  SELECT lesson_id, COUNT(*) AS sign_count " +
                "  FROM lesson_items " +
                "  GROUP BY lesson_id" +
                ") item_counts ON item_counts.lesson_id = l.id " +
                "ORDER BY l.id DESC";
        return jdbcTemplate.query(sql, lessonRowMapper);
    }

    public List<Lesson> findPublished() {
        String sql = "SELECT l.*, COALESCE(item_counts.sign_count, 0) AS sign_count " +
                "FROM lessons l " +
                "LEFT JOIN (" +
                "  SELECT lesson_id, COUNT(*) AS sign_count " +
                "  FROM lesson_items " +
                "  GROUP BY lesson_id" +
                ") item_counts ON item_counts.lesson_id = l.id " +
                "WHERE l.status = 'PUBLISHED' AND (l.publish_at IS NULL OR l.publish_at <= NOW()) " +
                "ORDER BY l.id DESC";
        return jdbcTemplate.query(sql, lessonRowMapper);
    }

    public Lesson findById(Long id) {
        String sql = "SELECT * FROM lessons WHERE id = ?";
        List<Lesson> results = jdbcTemplate.query(sql, lessonRowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public Lesson findBySlug(String slug) {
        String sql = "SELECT * FROM lessons WHERE slug = ?";
        List<Lesson> results = jdbcTemplate.query(sql, lessonRowMapper, slug);
        return results.isEmpty() ? null : results.get(0);
    }

    public Long insert(Lesson lesson) {
        String sql = "INSERT INTO lessons(topic_id, title, slug, description, cover_image, level, region, status, is_featured, publish_at, unpublish_at, created_by) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, lesson.getTopicId());
            ps.setString(2, lesson.getTitle());
            ps.setString(3, lesson.getSlug());
            ps.setString(4, lesson.getDescription());
            ps.setString(5, lesson.getCoverImage());
            ps.setString(6, lesson.getLevel() != null ? lesson.getLevel().name() : "BASIC");
            ps.setString(7, lesson.getRegion() != null ? lesson.getRegion().name() : "TOAN_QUOC");
            ps.setString(8, lesson.getStatus() != null ? lesson.getStatus().name() : "DRAFT");
            ps.setBoolean(9, lesson.isFeatured());
            ps.setObject(10, lesson.getPublishAt());
            ps.setObject(11, lesson.getUnpublishAt());
            ps.setObject(12, lesson.getCreatedBy());
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public int update(Lesson lesson) {
        String sql = "UPDATE lessons SET topic_id = ?, title = ?, slug = ?, description = ?, cover_image = ?, " +
                     "level = ?, region = ?, status = ?, is_featured = ?, publish_at = ?, unpublish_at = ? WHERE id = ?";
        return jdbcTemplate.update(sql, 
                lesson.getTopicId(), lesson.getTitle(), lesson.getSlug(), lesson.getDescription(), lesson.getCoverImage(),
                lesson.getLevel() != null ? lesson.getLevel().name() : "BASIC", 
                lesson.getRegion() != null ? lesson.getRegion().name() : "TOAN_QUOC", 
                lesson.getStatus() != null ? lesson.getStatus().name() : "DRAFT", 
                lesson.isFeatured(), lesson.getPublishAt(), lesson.getUnpublishAt(), lesson.getId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM lessons WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // Quản lý từ vựng trong bài học (Lesson Items)
    public void addSignToLesson(Long lessonId, Long signId, int displayOrder) {
        String sql = "INSERT INTO lesson_items (lesson_id, sign_id, display_order) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, lessonId, signId, displayOrder);
    }

    public void removeAllSignsFromLesson(Long lessonId) {
        String sql = "DELETE FROM lesson_items WHERE lesson_id = ?";
        jdbcTemplate.update(sql, lessonId);
    }

    public List<Long> findSignIdsByLessonId(Long lessonId) {
        String sql = "SELECT sign_id FROM lesson_items WHERE lesson_id = ? ORDER BY display_order ASC";
        return jdbcTemplate.queryForList(sql, Long.class, lessonId);
    }

    public int countSignsByLessonId(Long lessonId) {
        String sql = "SELECT COUNT(*) FROM lesson_items WHERE lesson_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, lessonId);
        return count != null ? count : 0;
    }
}
