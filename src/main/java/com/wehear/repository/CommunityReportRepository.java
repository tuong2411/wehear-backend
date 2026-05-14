package com.wehear.repository;

import com.wehear.model.CommunityReport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommunityReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommunityReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CommunityReport> rowMapper = (rs, rowNum) -> {
        CommunityReport report = new CommunityReport();
        report.setId(rs.getLong("id"));
        report.setReporterId(rs.getLong("reporter_id"));
        report.setPostId(rs.getObject("post_id", Long.class));
        report.setCommentId(rs.getObject("comment_id", Long.class));
        report.setReason(rs.getString("reason"));
        report.setStatus(rs.getString("status"));
        report.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return report;
    };

    public void save(CommunityReport report) {
        String sql = "INSERT INTO community_reports (reporter_id, post_id, comment_id, reason, status) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, 
                report.getReporterId(), 
                report.getPostId(), 
                report.getCommentId(), 
                report.getReason(), 
                report.getStatus() != null ? report.getStatus() : "PENDING");
    }

    public List<CommunityReport> findAll() {
        String sql = "SELECT * FROM community_reports ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void updateStatus(Long id, String status) {
        String sql = "UPDATE community_reports SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, id);
    }
}
