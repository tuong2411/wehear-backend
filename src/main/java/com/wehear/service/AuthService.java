package com.wehear.service;

import com.wehear.dto.AuthRequest;
import com.wehear.dto.AuthResponse;
import com.wehear.dto.RegisterRequest;
import com.wehear.model.User;
import com.wehear.repository.UserRepository;
import com.wehear.util.JwtTokenUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmailService emailService;

    private static class ResetTokenInfo {
        String email;
        long expiryTime;

        ResetTokenInfo(String email, long expiryTime) {
            this.email = email;
            this.expiryTime = expiryTime;
        }
    }

    private final Map<String, ResetTokenInfo> resetTokens = new ConcurrentHashMap<>();

    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendUrl;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.emailService = emailService;
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String token = UUID.randomUUID().toString();
        // Token expires in 15 minutes
        long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000);
        resetTokens.put(token, new ResetTokenInfo(email, expiryTime));

        String resetLink = frontendUrl + "/reset-password?token=" + token;
        String htmlBody = emailService.getResetPasswordTemplate(resetLink);
        
        emailService.sendHtmlEmail(email, "Đặt lại mật khẩu - Wehear", htmlBody);
    }

    public void resetPassword(String token, String newPassword) {
        ResetTokenInfo tokenInfo = resetTokens.get(token);
        if (tokenInfo == null) {
            throw new RuntimeException("Token không hợp lệ");
        }

        if (System.currentTimeMillis() > tokenInfo.expiryTime) {
            resetTokens.remove(token);
            throw new RuntimeException("Token đã hết hạn");
        }

        User user = userRepository.findByEmail(tokenInfo.email).get();
        userRepository.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
        resetTokens.remove(token);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        String token = jwtTokenUtil.generateToken(user);
        user.setPassword(null);
        return new AuthResponse(token, user);
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng");
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

        // Send Welcome Email
        try {
            String htmlBody = emailService.getWelcomeTemplate(user.getFullName());
            emailService.sendHtmlEmail(user.getEmail(), "Chào mừng bạn đến với Wehear!", htmlBody);
        } catch (Exception e) {
            // Log error but don't fail registration
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }
}
