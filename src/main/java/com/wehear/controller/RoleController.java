package com.wehear.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wehear.model.Role;
import com.wehear.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
	private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Role not found with id = " + id);
        }
        return ResponseEntity.ok(role);
    }

    @PostMapping
    public ResponseEntity<String> createRole(@RequestBody Role role) {
        boolean created = roleService.createRole(role);
        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Create role successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Create role failed");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateRole(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        boolean updated = roleService.updateRole(role);
        if (updated) {
            return ResponseEntity.ok("Update role successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update role failed");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        boolean deleted = roleService.deleteRole(id);
        if (deleted) {
            return ResponseEntity.ok("Delete role successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delete role failed");
    }

}
