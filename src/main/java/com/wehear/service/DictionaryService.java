package com.wehear.service;

import com.wehear.model.SignDictionary;
import com.wehear.model.SignMedia;
import com.wehear.repository.SignDictionaryRepository;
import com.wehear.repository.SignMediaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DictionaryService {

    private final SignDictionaryRepository signRepository;
    private final SignMediaRepository mediaRepository;
    private final com.wehear.util.CloudinaryUtil cloudinaryUtil;

    public DictionaryService(SignDictionaryRepository signRepository, SignMediaRepository mediaRepository, com.wehear.util.CloudinaryUtil cloudinaryUtil) {
        this.signRepository = signRepository;
        this.mediaRepository = mediaRepository;
        this.cloudinaryUtil = cloudinaryUtil;
    }

    public List<SignDictionary> getAllSigns() {
        List<SignDictionary> signs = signRepository.findAll();
        loadMediaForSigns(signs);
        return signs;
    }

    public List<SignDictionary> getPaginatedSigns(int page, int size, String search, String region) {
        int offset = page * size;
        List<SignDictionary> signs = signRepository.findAllPaginated(offset, size, search, region);
        loadMediaForSigns(signs);
        return signs;
    }

    public int getTotalCount(String search, String region) {
        return signRepository.countTotal(search, region);
    }

    private void loadMediaForSigns(List<SignDictionary> signs) {
        if (signs == null || signs.isEmpty()) {
            return;
        }
        List<Long> signIds = signs.stream().map(SignDictionary::getId).toList();
        List<SignMedia> allMedia = mediaRepository.findBySignIds(signIds);
        
        // Group media by signId for faster access
        for (SignDictionary sign : signs) {
            sign.setMedia(allMedia.stream()
                    .filter(m -> m.getSignId().equals(sign.getId()))
                    .toList());
        }
    }

    public SignDictionary getSignById(Long id) {
        SignDictionary sign = signRepository.findById(id);
        if (sign != null) {
            sign.setMedia(mediaRepository.findBySignId(sign.getId()));
        }
        return sign;
    }

    @Transactional
    public Long createSign(SignDictionary sign) {
        // Kiểm tra trùng lặp label_code
        if (signRepository.findByLabelCode(sign.getLabelCode()) != null) {
            throw new RuntimeException("Mã nhãn '" + sign.getLabelCode() + "' đã tồn tại trong hệ thống.");
        }
        
        Long signId = signRepository.insert(sign);
        if (signId != null) {
            if (sign.getMedia() != null) {
                for (SignMedia media : sign.getMedia()) {
                    media.setSignId(signId);
                    mediaRepository.insert(media);
                }
            }
            return signId;
        }
        return null;
    }

    @Transactional
    public boolean updateSign(SignDictionary sign) {
        int updated = signRepository.update(sign);
        if (updated > 0) {
            // Update media: For simplicity in this CRUD, we replace all media
            // In a real scenario, you might want more granular control
            if (sign.getMedia() != null) {
                mediaRepository.deleteBySignId(sign.getId());
                for (SignMedia media : sign.getMedia()) {
                    media.setSignId(sign.getId());
                    mediaRepository.insert(media);
                }
            }
            return true;
        }
        return false;
    }

    public boolean updateStatus(Long id, boolean active) {
        return signRepository.updateStatus(id, active) > 0;
    }

    @Transactional
    public void bulkAction(List<Long> ids, String action) {
        if (ids == null || ids.isEmpty()) return;
        
        if ("delete".equals(action)) {
            for (Long id : ids) {
                mediaRepository.deleteBySignId(id);
            }
            signRepository.bulkDelete(ids);
        } else if ("activate".equals(action)) {
            for (Long id : ids) {
                signRepository.updateStatus(id, true);
            }
        } else if ("deactivate".equals(action)) {
            for (Long id : ids) {
                signRepository.updateStatus(id, false);
            }
        }
    }

    @Transactional
    public boolean deleteSign(Long id) {
        mediaRepository.deleteBySignId(id);
        return signRepository.deleteById(id) > 0;
    }

    @Transactional
    public void deleteSignMedia(Long signId) {
        mediaRepository.deleteBySignId(signId);
    }

    public String storeMedia(org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        return cloudinaryUtil.uploadByType(file, "dictionary");
    }

    @Transactional
    public void addMedia(Long signId, String mediaUrl, String type, boolean isPrimary) {
        SignMedia media = new SignMedia();
        media.setSignId(signId);
        media.setMediaUrl(mediaUrl);
        media.setMediaType(type);
        media.setPrimary(isPrimary);
        mediaRepository.insert(media);
    }
}
