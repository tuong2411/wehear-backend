package com.wehear.service;

import com.wehear.model.VslUploadVideo;
import com.wehear.repository.VslUploadVideoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VslUploadVideoService {

    private static final int MAX_LABEL_LENGTH = 255;

    private final VslUploadVideoRepository repository;

    public VslUploadVideoService(VslUploadVideoRepository repository) {
        this.repository = repository;
    }

    public Long saveUpload(Long userId, String videoUrl, String selectedLabel, Double confidence) {
        String normalizedLabel = requireLabel(selectedLabel);
        String normalizedUrl = videoUrl == null ? "" : videoUrl.trim();

        if (normalizedUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL video không được để trống.");
        }

        VslUploadVideo item = new VslUploadVideo();
        item.setUserId(userId);
        item.setVideoUrl(normalizedUrl);
        item.setSelectedLabel(normalizedLabel);
        item.setConfidence(confidence);

        return repository.save(item);
    }

    public List<VslUploadVideo> getRecentUploads(int limit) {
        int boundedLimit = Math.max(1, Math.min(limit, 1000));
        return repository.findRecent(boundedLimit);
    }

    private String requireLabel(String value) {
        String normalized = value == null ? "" : value.trim().replaceAll("\\s+", " ");

        if (normalized.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Từ nhận diện không được để trống.");
        }

        if (normalized.length() > MAX_LABEL_LENGTH) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Từ nhận diện không được vượt quá " + MAX_LABEL_LENGTH + " ký tự."
            );
        }

        return normalized;
    }
}
