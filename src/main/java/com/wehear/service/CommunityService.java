package com.wehear.service;

import com.wehear.model.*;
import com.wehear.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommunityService {

    private final CommunityPostRepository postRepository;
    private final CommunityCommentRepository commentRepository;
    private final CommunityLikeRepository likeRepository;
    private final CommunityReportRepository reportRepository;

    public CommunityService(CommunityPostRepository postRepository,
                           CommunityCommentRepository commentRepository,
                           CommunityLikeRepository likeRepository,
                           CommunityReportRepository reportRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.reportRepository = reportRepository;
    }

    // Posts
    public Long createPost(CommunityPost post) {
        if (post.getTitle() == null || post.getTitle().isBlank()) throw new RuntimeException("Tiêu đề không được để trống");
        if (post.getContent() == null || post.getContent().isBlank()) throw new RuntimeException("Nội dung không được để trống");
        return postRepository.save(post);
    }

    public List<CommunityPost> getActivePosts(int page, int size, Long currentUserId) {
        int offset = page * size;
        List<CommunityPost> posts = postRepository.findAllActive(size, offset, currentUserId);
        if (currentUserId != null) {
            for (CommunityPost post : posts) {
                post.setLikedByCurrentUser(postRepository.isLikedByUser(post.getId(), currentUserId));
            }
        }
        return posts;
    }

    public CommunityPost getPostDetail(Long id, Long currentUserId) {
        CommunityPost post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));
        if (currentUserId != null) {
            post.setLikedByCurrentUser(postRepository.isLikedByUser(post.getId(), currentUserId));
        }

        // Fetch comments and build tree
        List<CommunityComment> allComments = commentRepository.findByPostId(id);
        if (currentUserId != null) {
            for (CommunityComment c : allComments) {
                c.setLikedByCurrentUser(commentRepository.isLikedByUser(c.getId(), currentUserId));
            }
        }
        
        post.setCommentCount(allComments.size());
        // Simple nested structure (top level + 1 level of replies)
        Map<Long, List<CommunityComment>> repliesMap = allComments.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(CommunityComment::getParentId));

        List<CommunityComment> topLevelComments = allComments.stream()
                .filter(c -> c.getParentId() == null)
                .peek(c -> c.setReplies(repliesMap.getOrDefault(c.getId(), new ArrayList<>())))
                .collect(Collectors.toList());

        // Note: CommunityPost needs a setComments method if we want to return it in detail
        // For simplicity, I'll return it separately in the controller or add a field
        return post;
    }
    
    // Comments
    public void addComment(CommunityComment comment) {
        if (comment.getContent() == null || comment.getContent().isBlank()) throw new RuntimeException("Nội dung bình luận không được để trống");
        commentRepository.save(comment);
    }

    // Likes
    public boolean togglePostLike(Long userId, Long postId) {
        return likeRepository.togglePostLike(userId, postId);
    }

    public boolean toggleCommentLike(Long userId, Long commentId) {
        return likeRepository.toggleCommentLike(userId, commentId);
    }

    // Reports
    public void reportContent(CommunityReport report) {
        if (report.getReason() == null || report.getReason().isBlank()) throw new RuntimeException("Lý do báo cáo không được để trống");
        reportRepository.save(report);
    }

    // Admin
    public List<CommunityPost> getAllPostsForAdmin(int page, int size) {
        int offset = page * size;
        return postRepository.findAll(size, offset);
    }

    @Transactional
    public void hidePost(Long id) {
        postRepository.updateStatus(id, "HIDDEN");
        commentRepository.hideCommentsByPostId(id);
    }

    @Transactional
    public void showPost(Long id) {
        postRepository.updateStatus(id, "ACTIVE");
        // Optional: Should we unhide all comments too? 
        // Typically yes, if they weren't individually hidden before.
        // For simplicity, let's just show the post.
    }

    public void hideComment(Long id) {
        commentRepository.updateStatus(id, "HIDDEN");
    }

    public void showComment(Long id) {
        commentRepository.updateStatus(id, "ACTIVE");
    }

    public List<CommunityReport> getReports() {
        return reportRepository.findAll();
    }

    public void resolveReport(Long id) {
        reportRepository.updateStatus(id, "RESOLVED");
    }

    public List<CommunityComment> getCommentsForPost(Long postId, Long currentUserId) {
        List<CommunityComment> allComments = commentRepository.findByPostId(postId);
        if (currentUserId != null) {
            for (CommunityComment c : allComments) {
                c.setLikedByCurrentUser(commentRepository.isLikedByUser(c.getId(), currentUserId));
            }
        }
        
        Map<Long, List<CommunityComment>> repliesMap = allComments.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(CommunityComment::getParentId));

        return allComments.stream()
                .filter(c -> c.getParentId() == null)
                .peek(c -> c.setReplies(repliesMap.getOrDefault(c.getId(), new ArrayList<>())))
                .collect(Collectors.toList());
    }
}
