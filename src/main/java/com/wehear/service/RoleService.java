package com.wehear.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wehear.model.Role;
import com.wehear.repository.RoleRepository;

@Service
public class RoleService {

	private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public boolean createRole(Role role) {
        return roleRepository.insert(role) > 0;
    }

    public boolean updateRole(Role role) {
        return roleRepository.update(role) > 0;
    }

    public boolean deleteRole(Long id) {
        return roleRepository.deleteById(id) > 0;
    }
}
