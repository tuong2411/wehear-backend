package com.wehear.repository;

import com.wehear.model.Quiz;
import com.wehear.model.QuizQuestion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class QuizRepository {

    private final JdbcTemplate jdbcTemplate;

    public QuizRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Quiz findByLessonId(Long lessonId) {
        String sql = "SELECT * FROM quizzes WHERE lesson_id = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getLong("id"));
                quiz.setLessonId(rs.getLong("lesson_id"));
                quiz.setTitle(rs.getString("title"));
                quiz.setDescription(rs.getString("description"));
                
                int timeLimit = rs.getInt("time_limit_minutes");
                if (!rs.wasNull()) {
                    quiz.setTimeLimitMinutes(timeLimit);
                }
                
                quiz.setPassingScore(rs.getBigDecimal("passing_score"));
                quiz.setStatus(rs.getBoolean("status"));
                return quiz;
            }
            return null;
        }, lessonId);
    }

    public List<QuizQuestion> findQuestionsByQuizId(Long quizId) {
        String sql = "SELECT * FROM quiz_questions WHERE quiz_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            QuizQuestion q = new QuizQuestion();
            q.setId(rs.getLong("id"));
            q.setQuizId(rs.getLong("quiz_id"));
            q.setQuestionText(rs.getString("question_text"));
            q.setQuestionType(rs.getString("question_type"));
            q.setOptionA(rs.getString("option_a"));
            q.setOptionB(rs.getString("option_b"));
            q.setOptionC(rs.getString("option_c"));
            q.setOptionD(rs.getString("option_d"));
            q.setCorrectAnswer(rs.getString("correct_answer"));
            
            long relatedSignId = rs.getLong("related_sign_id");
            if (!rs.wasNull()) {
                q.setRelatedSignId(relatedSignId);
            } else {
                q.setRelatedSignId(null);
            }
            
            return q;
        }, quizId);
    }

    public Long insertQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes(lesson_id, title, description, time_limit_minutes, passing_score, status) VALUES(?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, quiz.getLessonId());
            ps.setString(2, quiz.getTitle());
            ps.setString(3, quiz.getDescription());
            ps.setInt(4, quiz.getTimeLimitMinutes());
            ps.setBigDecimal(5, quiz.getPassingScore());
            ps.setBoolean(6, quiz.isStatus());
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public void updateQuiz(Quiz quiz) {
        String sql = "UPDATE quizzes SET title = ?, description = ?, time_limit_minutes = ?, passing_score = ?, status = ? WHERE id = ?";
        jdbcTemplate.update(sql, quiz.getTitle(), quiz.getDescription(), quiz.getTimeLimitMinutes(), quiz.getPassingScore(), quiz.isStatus(), quiz.getId());
    }

    public void deleteQuizByLessonId(Long lessonId) {
        String sql = "DELETE FROM quizzes WHERE lesson_id = ?";
        jdbcTemplate.update(sql, lessonId);
    }

    public void insertQuestion(QuizQuestion q) {
        String sql = "INSERT INTO quiz_questions(quiz_id, question_text, question_type, option_a, option_b, option_c, option_d, correct_answer, related_sign_id) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, q.getQuizId(), q.getQuestionText(), q.getQuestionType(), q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(), q.getCorrectAnswer(), q.getRelatedSignId());
    }

    public void updateQuestion(QuizQuestion q) {
        String sql = "UPDATE quiz_questions SET question_text = ?, question_type = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_answer = ?, related_sign_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, q.getQuestionText(), q.getQuestionType(), q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(), q.getCorrectAnswer(), q.getRelatedSignId(), q.getId());
    }

    public void deleteQuestionIfNotAttempted(Long questionId) {
        // Kiểm tra xem câu hỏi đã được làm bài chưa
        String checkSql = "SELECT COUNT(*) FROM quiz_attempt_answers WHERE question_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, questionId);
        
        if (count != null && count == 0) {
            String deleteSql = "DELETE FROM quiz_questions WHERE id = ?";
            jdbcTemplate.update(deleteSql, questionId);
        }
        // Nếu đã được làm bài (count > 0), chúng ta không xóa để giữ toàn vẹn dữ liệu
    }

    public void deleteQuestionsByQuizId(Long quizId) {
        // Thay vì xóa tất cả, chúng ta nên lọc và xóa từng cái hoặc xử lý an toàn hơn
        // Nhưng phương thức này vẫn giữ lại để dùng cho trường hợp xóa toàn bộ bài học
        String sql = "DELETE FROM quiz_questions WHERE quiz_id = ?";
        jdbcTemplate.update(sql, quizId);
    }
}
