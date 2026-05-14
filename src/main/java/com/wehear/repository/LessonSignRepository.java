package com.wehear.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LessonSignRepository {

    private final JdbcTemplate jdbcTemplate;

    public LessonSignRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Long> findSignIdsByLessonId(Long lessonId) {
        String sql = "SELECT sign_id FROM lesson_signs WHERE lesson_id = ? ORDER BY display_order ASC";
        return jdbcTemplate.queryForList(sql, Long.class, lessonId);
    }

    public int insert(Long lessonId, Long signId, int displayOrder) {
        String sql = "INSERT INTO lesson_signs(lesson_id, sign_id, display_order) VALUES(?, ?, ?)";
        return jdbcTemplate.update(sql, lessonId, signId, displayOrder);
    }

    public int deleteByLessonId(Long lessonId) {
        String sql = "DELETE FROM lesson_signs WHERE lesson_id = ?";
        return jdbcTemplate.update(sql, lessonId);
    }
}
