package com.wehear.service;

import com.wehear.dto.DashboardStats;
import com.wehear.model.Lesson;
import com.wehear.repository.mapper.LessonRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final JdbcTemplate jdbcTemplate;
    private final LessonRowMapper lessonRowMapper;

    public DashboardService(JdbcTemplate jdbcTemplate, LessonRowMapper lessonRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.lessonRowMapper = lessonRowMapper;
    }

    public DashboardStats getStats() {
        DashboardStats stats = new DashboardStats();
        
        // Count totals
        stats.setTotalUsers(countTable("users"));
        stats.setTotalLessons(countTable("lessons"));
        stats.setTotalSigns(countTable("sign_dictionary"));
        stats.setTotalQuizzes(countTable("quizzes"));
        
        // Get recent updates (top 5 lessons)
        String recentLessonsSql = "SELECT * FROM lessons ORDER BY id DESC LIMIT 5";
        List<Lesson> recentLessons = jdbcTemplate.query(recentLessonsSql, lessonRowMapper);
        stats.setRecentLessons(recentLessons);
        
        return stats;
    }

    private long countTable(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }
}
