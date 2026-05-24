package com.wehear.controller;

import com.wehear.dto.BulkActionRequest;
import com.wehear.dto.RegisterRequest;
import com.wehear.model.User;
import com.wehear.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .roleId(2L)
                .status(1)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("User created successfully");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        if (isCurrentUser(id) && Integer.valueOf(0).equals(status)) {
            return ResponseEntity.badRequest().body("Không thể tự khóa tài khoản của chính mình");
        }

        userRepository.updateStatus(id, status);
        return ResponseEntity.ok("User status updated successfully");
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        if (isCurrentUser(id)) {
            return ResponseEntity.badRequest().body("Không thể tự thay đổi vai trò của chính mình");
        }

        Long roleId = body.get("roleId");
        if (roleId == null || (roleId != 1L && roleId != 2L)) {
            return ResponseEntity.badRequest().body("Invalid role");
        }

        userRepository.updateRole(id, roleId);
        return ResponseEntity.ok("User role updated successfully");
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<String> resetPassword(@PathVariable Long id) {
        userRepository.updatePassword(id, passwordEncoder.encode("123456"));
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/bulk-action")
    public ResponseEntity<String> bulkAction(@RequestBody BulkActionRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return ResponseEntity.badRequest().body("No users selected");
        }

        Long currentUserId = getCurrentUserId();
        if (currentUserId != null && request.getIds().contains(currentUserId)) {
            if ("lock".equals(request.getAction())) {
                return ResponseEntity.badRequest().body("Không thể tự khóa tài khoản của chính mình");
            }
            if ("delete".equals(request.getAction())) {
                return ResponseEntity.badRequest().body("Không thể tự xóa tài khoản của chính mình");
            }
        }

        for (Long id : request.getIds()) {
            switch (request.getAction()) {
                case "lock":
                    userRepository.updateStatus(id, 0);
                    break;
                case "unlock":
                    userRepository.updateStatus(id, 1);
                    break;
                case "delete":
                    userRepository.deleteById(id);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid action");
            }
        }

        return ResponseEntity.ok("Bulk action completed successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (isCurrentUser(id)) {
            return ResponseEntity.badRequest().body("Không thể tự xóa tài khoản của chính mình");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    private boolean isCurrentUser(Long id) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(id);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        return userRepository.findByUsername(authentication.getName())
                .map(User::getId)
                .orElse(null);
    }
}
