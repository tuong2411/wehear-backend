package com.wehear.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.wehear.model.Role;
import com.wehear.repository.mapper.RoleRowMapper;

@Repository
public class RoleRepository {

	private final JdbcTemplate jdbcTemplate;
    private final RoleRowMapper roleRowMapper;
    
    public RoleRepository(JdbcTemplate jdbcTemplate, RoleRowMapper roleRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.roleRowMapper = roleRowMapper;
    }
    
    public List<Role> findAll() {
        String sql = "SELECT id, name, description FROM roles ORDER BY id ASC";
        return jdbcTemplate.query(sql, roleRowMapper);
    }
    public Role findById(Long id) {
        String sql = "SELECT id, name, description FROM roles WHERE id = ?";
        List<Role> roles = jdbcTemplate.query(sql, roleRowMapper, id);
        return roles.isEmpty() ? null : roles.get(0);
    }

    public int insert(Role role) {
        String sql = "INSERT INTO roles(name, description) VALUES(?, ?)";
        return jdbcTemplate.update(sql, role.getName(), role.getDescription());
    }

    public int update(Role role) {
        String sql = "UPDATE roles SET name = ?, description = ? WHERE id = ?";
        return jdbcTemplate.update(sql, role.getName(), role.getDescription(), role.getId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM roles WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
