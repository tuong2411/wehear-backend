package com.wehear.repository;

import com.wehear.model.User;
import com.wehear.repository.mapper.UserRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper = new UserRowMapper();

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT u.*, r.name as role_name FROM users u " +
                     "JOIN roles r ON u.role_id = r.id WHERE u.username = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, username));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT u.*, r.name as role_name FROM users u " +
                     "JOIN roles r ON u.role_id = r.id WHERE u.email = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT u.*, r.name as role_name FROM users u " +
                     "JOIN roles r ON u.role_id = r.id WHERE u.id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        String sql = "SELECT u.*, r.name as role_name FROM users u " +
                     "JOIN roles r ON u.role_id = r.id ORDER BY u.created_at DESC";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public void updateStatus(Long userId, Integer status) {
        String sql = "UPDATE users SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        jdbcTemplate.update(sql, status, userId);
    }

    public void updateRole(Long userId, Long roleId) {
        String sql = "UPDATE users SET role_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        jdbcTemplate.update(sql, roleId, userId);
    }

    public void deleteById(Long userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, userId);
    }

    public void save(User user) {
        String sql = "INSERT INTO users (username, password, email, full_name, phone_number, role_id, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, 
                user.getUsername(), 
                user.getPassword(), 
                user.getEmail(), 
                user.getFullName(), 
                user.getPhoneNumber(), 
                user.getRoleId(), 
                user.getStatus());
    }

    public void updatePassword(Long userId, String encodedPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        jdbcTemplate.update(sql, encodedPassword, userId);
    }

    public void updateProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, phone_number = ?, avatar_url = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getFullName(), user.getPhoneNumber(), user.getAvatarUrl(), user.getId());
    }
}
