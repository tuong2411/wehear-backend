package com.wehear.dto;

import com.wehear.model.Lesson;
import lombok.Data;
import java.util.List;

@Data
public class DashboardStats {
    private long totalUsers;
    private long totalLessons;
    private long totalSigns;
    private long totalQuizzes;
    
    private List<Lesson> recentLessons;

    // Manual getters and setters just in case Lombok is not working as expected
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalLessons() { return totalLessons; }
    public void setTotalLessons(long totalLessons) { this.totalLessons = totalLessons; }
    public long getTotalSigns() { return totalSigns; }
    public void setTotalSigns(long totalSigns) { this.totalSigns = totalSigns; }
    public long getTotalQuizzes() { return totalQuizzes; }
    public void setTotalQuizzes(long totalQuizzes) { this.totalQuizzes = totalQuizzes; }
    public List<Lesson> getRecentLessons() { return recentLessons; }
    public void setRecentLessons(List<Lesson> recentLessons) { this.recentLessons = recentLessons; }
}
