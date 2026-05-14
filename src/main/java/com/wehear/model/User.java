package com.wehear.model;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private Long roleId;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String roleName;

    public User() {}

    public User(Long id, String username, String password, String email, String fullName, String phoneNumber, String avatarUrl, Long roleId, Integer status, LocalDateTime createdAt, LocalDateTime updatedAt, String roleName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
        this.roleId = roleId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roleName = roleName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private User instance = new User();

        public UserBuilder id(Long id) { instance.setId(id); return this; }
        public UserBuilder username(String username) { instance.setUsername(username); return this; }
        public UserBuilder password(String password) { instance.setPassword(password); return this; }
        public UserBuilder email(String email) { instance.setEmail(email); return this; }
        public UserBuilder fullName(String fullName) { instance.setFullName(fullName); return this; }
        public UserBuilder phoneNumber(String phoneNumber) { instance.setPhoneNumber(phoneNumber); return this; }
        public UserBuilder avatarUrl(String avatarUrl) { instance.setAvatarUrl(avatarUrl); return this; }
        public UserBuilder roleId(Long roleId) { instance.setRoleId(roleId); return this; }
        public UserBuilder status(Integer status) { instance.setStatus(status); return this; }
        public UserBuilder createdAt(LocalDateTime createdAt) { instance.setCreatedAt(createdAt); return this; }
        public UserBuilder updatedAt(LocalDateTime updatedAt) { instance.setUpdatedAt(updatedAt); return this; }
        public UserBuilder roleName(String roleName) { instance.setRoleName(roleName); return this; }

        public User build() { return instance; }
    }
}
