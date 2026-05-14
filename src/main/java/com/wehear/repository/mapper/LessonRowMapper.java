package com.wehear.repository.mapper;

import com.wehear.model.Lesson;
import com.wehear.model.LessonLevel;
import com.wehear.model.LessonRegion;
import com.wehear.model.LessonStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LessonRowMapper implements RowMapper<Lesson> {
    @Override
    public Lesson mapRow(ResultSet rs, int rowNum) throws SQLException {
        Lesson lesson = new Lesson();
        lesson.setId(rs.getLong("id"));
        lesson.setTopicId(rs.getObject("topic_id", Long.class));
        lesson.setTitle(rs.getString("title"));
        lesson.setSlug(rs.getString("slug"));
        lesson.setDescription(rs.getString("description"));
        lesson.setCoverImage(rs.getString("cover_image"));
        
        try {
            lesson.setLevel(LessonLevel.valueOf(rs.getString("level")));
        } catch (Exception e) {
            lesson.setLevel(LessonLevel.BASIC);
        }

        try {
            lesson.setRegion(LessonRegion.valueOf(rs.getString("region")));
        } catch (Exception e) {
            lesson.setRegion(LessonRegion.TOAN_QUOC);
        }

        try {
            lesson.setStatus(LessonStatus.valueOf(rs.getString("status")));
        } catch (Exception e) {
            lesson.setStatus(LessonStatus.DRAFT);
        }

        lesson.setFeatured(rs.getBoolean("is_featured"));
        lesson.setPublishAt(rs.getTimestamp("publish_at") != null ? rs.getTimestamp("publish_at").toLocalDateTime() : null);
        lesson.setUnpublishAt(rs.getTimestamp("unpublish_at") != null ? rs.getTimestamp("unpublish_at").toLocalDateTime() : null);
        
        long createdBy = rs.getLong("created_by");
        if (!rs.wasNull()) {
            lesson.setCreatedBy(createdBy);
        } else {
            lesson.setCreatedBy(null);
        }
        
        lesson.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        lesson.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        
        return lesson;
    }
}
