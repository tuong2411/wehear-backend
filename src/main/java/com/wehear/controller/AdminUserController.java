package com.wehear.controller;

import com.wehear.model.User;
import com.wehear.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        // We'll need to add a findAll method to UserRepository
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userRepository.updateStatus(id, status);
        return ResponseEntity.ok("Cập nhật trạng thái người dùng thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("Xóa người dùng thành công");
    }
}
