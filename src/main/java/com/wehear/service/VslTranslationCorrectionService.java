package com.wehear.service;

import com.wehear.dto.VslTranslationCorrectionRequest;
import com.wehear.model.VslTranslationCorrection;
import com.wehear.repository.VslTranslationCorrectionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VslTranslationCorrectionService {

    private static final int MAX_TEXT_LENGTH = 1000;

    private final VslTranslationCorrectionRepository repository;

    public VslTranslationCorrectionService(VslTranslationCorrectionRepository repository) {
        this.repository = repository;
    }

    public Long saveCorrection(Long userId, VslTranslationCorrectionRequest request) {
        VslTranslationCorrection correction = new VslTranslationCorrection();
        correction.setUserId(userId);
        correction.setSourceText(requireText(request.getSourceText(), "Chuỗi VSL đầu vào"));
        correction.setModelName(requireText(request.getModelName(), "Tên model"));
        correction.setModelTranslation(requireText(request.getModelTranslation(), "Kết quả model"));
        correction.setCorrectedTranslation(requireText(request.getCorrectedTranslation(), "Bản người dùng chỉnh sửa"));

        return repository.save(correction);
    }

    public List<VslTranslationCorrection> getMyCorrections(Long userId) {
        return repository.findByUserId(userId);
    }

    public List<VslTranslationCorrection> getRecentTrainingData(int limit) {
        int boundedLimit = Math.max(1, Math.min(limit, 1000));
        return repository.findRecentForTraining(boundedLimit);
    }

    private String requireText(String value, String fieldName) {
        String normalized = value == null ? "" : value.trim().replaceAll("\\s+", " ");

        if (normalized.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " không được để trống.");
        }

        if (normalized.length() > MAX_TEXT_LENGTH) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    fieldName + " không được vượt quá " + MAX_TEXT_LENGTH + " ký tự."
            );
        }

        return normalized;
    }
}
