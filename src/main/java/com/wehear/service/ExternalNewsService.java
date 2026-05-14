package com.wehear.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wehear.model.ExternalNewsArticle;
import com.wehear.repository.ExternalNewsRepository;

@Service
public class ExternalNewsService {

	private final ExternalNewsRepository externalNewsRepository;

    public ExternalNewsService(ExternalNewsRepository externalNewsRepository) {
        this.externalNewsRepository = externalNewsRepository;
    }

    public List<ExternalNewsArticle> getAllActiveNews() {
        return externalNewsRepository.findAllActive();
    }

    public List<ExternalNewsArticle> getAllNews() {
        return externalNewsRepository.findAll();
    }

    public List<ExternalNewsArticle> getPagedNews(int page, int size) {
        int offset = (page - 1) * size;
        return externalNewsRepository.findAllPaged(size, offset);
    }

    public int getTotalCount() {
        return externalNewsRepository.countAll();
    }

    public ExternalNewsArticle getNewsBySlug(String slug) {
        return externalNewsRepository.findBySlug(slug);
    }

    public List<ExternalNewsArticle> getNewsBySourceId(Long sourceId) {
        return externalNewsRepository.findBySourceId(sourceId);
    }

    public ExternalNewsArticle getNewsById(Long id) {
        return externalNewsRepository.findById(id);
    }

    public void createNews(ExternalNewsArticle article) {
        externalNewsRepository.insert(article);
    }

    public void updateNews(ExternalNewsArticle article) {
        externalNewsRepository.update(article);
    }

    public void deleteNews(Long id) {
        externalNewsRepository.delete(id);
    }
}
