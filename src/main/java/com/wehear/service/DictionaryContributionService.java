package com.wehear.service;

import com.wehear.model.DictionaryContribution;
import com.wehear.model.SignDictionary;
import com.wehear.model.SignMedia;
import com.wehear.model.User;
import com.wehear.repository.DictionaryContributionRepository;
import com.wehear.repository.SignDictionaryRepository;
import com.wehear.repository.SignMediaRepository;
import com.wehear.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DictionaryContributionService {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryContributionService.class);

    private final DictionaryContributionRepository contributionRepository;
    private final SignDictionaryRepository dictionaryRepository;
    private final SignMediaRepository mediaRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final com.wehear.util.CloudinaryUtil cloudinaryUtil;

    public DictionaryContributionService(DictionaryContributionRepository contributionRepository,
                                       SignDictionaryRepository dictionaryRepository,
                                       SignMediaRepository mediaRepository,
                                       UserRepository userRepository,
                                       EmailService emailService,
                                       com.wehear.util.CloudinaryUtil cloudinaryUtil) {
        this.contributionRepository = contributionRepository;
        this.dictionaryRepository = dictionaryRepository;
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.cloudinaryUtil = cloudinaryUtil;
    }

    public Long createContribution(DictionaryContribution contribution) {
        String word = contribution.getWord() != null ? contribution.getWord().trim() : "";
        contribution.setWord(word);
        contribution.setDescription(contribution.getDescription() != null ? contribution.getDescription().trim() : "");
        contribution.setExample(contribution.getExample() != null ? contribution.getExample().trim() : "");
        
        System.out.println("Creating contribution: type=" + contribution.getType() + ", word='" + word + "', targetId=" + contribution.getTargetDictionaryId());
        validateContribution(contribution);

        // Kiểm tra xem từ đã tồn tại trong từ điển chưa nếu loại là NEW
        if ("NEW".equals(contribution.getType())) {
            contribution.setTargetDictionaryId(null); // Ensure targetId is null for NEW
            List<SignDictionary> existing = dictionaryRepository.findBySignWordExact(word);
            if (!existing.isEmpty()) {
                throw new RuntimeException("Từ '" + word + "' đã tồn tại trong từ điển. Vui lòng chọn 'Góp ý chỉnh sửa' thay vì thêm từ mới.");
            }
        } else if ("EDIT".equals(contribution.getType())) {
            if (contribution.getTargetDictionaryId() != null
                    && dictionaryRepository.findById(contribution.getTargetDictionaryId()) == null) {
                throw new RuntimeException("Không tìm thấy từ điển cần chỉnh sửa.");
            }
            if (contribution.getTargetDictionaryId() == null) {
                throw new RuntimeException("Thiếu ID từ điển cần chỉnh sửa (targetDictionaryId).");
            }
        } else {
            throw new RuntimeException("Loại đóng góp không hợp lệ: " + contribution.getType());
        }
        
        contribution.setStatus("PENDING");
        Long id = contributionRepository.save(contribution);
        System.out.println("Contribution saved with ID: " + id);
        return id;
    }

    public String storeContributionVideo(org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        validateContributionVideo(file);
        return cloudinaryUtil.uploadByType(file, "dictionary");
    }

    public List<DictionaryContribution> getMyContributions(Long userId) {
        return contributionRepository.findByUserId(userId);
    }

    public List<DictionaryContribution> getPendingContributions() {
        return contributionRepository.findByStatus("PENDING");
    }

    public DictionaryContribution getContributionById(Long id) {
        return contributionRepository.findById(id).orElse(null);
    }

    private void validateContribution(DictionaryContribution contribution) {
        if (contribution.getWord() == null || contribution.getWord().isBlank()) {
            throw new RuntimeException("Vui lòng nhập từ vựng.");
        }
        if (contribution.getDescription() == null || contribution.getDescription().isBlank()) {
            throw new RuntimeException("Vui lòng nhập mô tả cách thực hiện.");
        }
        if (contribution.getVideoUrl() == null || contribution.getVideoUrl().isBlank()) {
            throw new RuntimeException("Vui lòng tải lên video minh họa.");
        }
        if (contribution.getType() == null || contribution.getType().isBlank()) {
            throw new RuntimeException("Vui lòng chọn loại đóng góp.");
        }
    }

    private void validateContributionVideo(org.springframework.web.multipart.MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn video minh họa.");
        }
        if (file.getSize() > 20 * 1024 * 1024) {
            throw new RuntimeException("Video quá lớn. Vui lòng chọn file dưới 20MB.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("video/")) {
            throw new RuntimeException("File tải lên phải là video.");
        }
    }

    @Transactional
    public void approveContribution(Long id, String adminNote) {
        DictionaryContribution contribution = contributionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contribution not found"));

        if (!"PENDING".equals(contribution.getStatus())) {
            throw new RuntimeException("Contribution is already processed");
        }

        Long finalSignId;
        if ("NEW".equals(contribution.getType())) {
            // Create new sign
            SignDictionary newSign = new SignDictionary();
            newSign.setSignWord(contribution.getWord());
            newSign.setDescription(contribution.getDescription());
            newSign.setExampleSentence(contribution.getExample());
            newSign.setActive(true);
            newSign.setCreatedBy(contribution.getUserId());
            
            // Generate a slug-like labelCode
            String baseLabel = contribution.getWord().toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .replaceAll("\\s+", "_");
            newSign.setLabelCode(baseLabel + "_" + System.currentTimeMillis());
            
            newSign.setRegion("Toàn quốc");
            newSign.setDifficultyLevel("Medium");

            finalSignId = dictionaryRepository.insert(newSign);
            System.out.println("Inserted new sign with ID: " + finalSignId);
        } else if ("EDIT".equals(contribution.getType())) {
            // Update existing sign
            SignDictionary existingSign = dictionaryRepository.findById(contribution.getTargetDictionaryId());
            if (existingSign == null) {
                throw new RuntimeException("Target dictionary entry not found");
            }

            existingSign.setSignWord(contribution.getWord());
            existingSign.setDescription(contribution.getDescription());
            existingSign.setExampleSentence(contribution.getExample());
            
            dictionaryRepository.update(existingSign);
            finalSignId = existingSign.getId();
            System.out.println("Updated existing sign with ID: " + finalSignId);
        } else {
            throw new RuntimeException("Unknown contribution type: " + contribution.getType());
        }

        // Add or Update Media
        if (contribution.getVideoUrl() != null && !contribution.getVideoUrl().isEmpty()) {
            mediaRepository.clearPrimaryBySignId(finalSignId);

            SignMedia media = new SignMedia();
            media.setSignId(finalSignId);
            media.setMediaUrl(contribution.getVideoUrl());
            media.setMediaType("VIDEO");
            media.setPrimary(true);
            mediaRepository.insert(media);
            System.out.println("Associated media with sign ID: " + finalSignId);
        }

        contributionRepository.updateStatus(id, "APPROVED", adminNote);

        sendApprovalEmail(contribution);
    }

    public void rejectContribution(Long id, String adminNote) {
        DictionaryContribution contribution = contributionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contribution not found"));

        if (!"PENDING".equals(contribution.getStatus())) {
            throw new RuntimeException("Contribution is already processed");
        }

        contributionRepository.updateStatus(id, "REJECTED", adminNote);

        sendRejectionEmail(contribution, adminNote);
    }

    private void sendApprovalEmail(DictionaryContribution contribution) {
        try {
            userRepository.findById(contribution.getUserId()).ifPresent(user -> {
                String html = emailService.getContributionApprovalTemplate(user.getFullName(), contribution.getWord());
                emailService.sendHtmlEmail(user.getEmail(), "Đóng góp của bạn đã được duyệt - WeHear", html);
            });
        } catch (Exception e) {
            logger.warn("Failed to send approval email for contribution {}", contribution.getId(), e);
        }
    }

    private void sendRejectionEmail(DictionaryContribution contribution, String adminNote) {
        try {
            userRepository.findById(contribution.getUserId()).ifPresent(user -> {
                String html = emailService.getContributionRejectionTemplate(user.getFullName(), contribution.getWord(), adminNote);
                emailService.sendHtmlEmail(user.getEmail(), "Cập nhật về đóng góp của bạn - WeHear", html);
            });
        } catch (Exception e) {
            logger.warn("Failed to send rejection email for contribution {}", contribution.getId(), e);
        }
    }
}
