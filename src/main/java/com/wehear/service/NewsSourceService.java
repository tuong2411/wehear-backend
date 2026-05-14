package com.wehear.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wehear.model.NewsSource;
import com.wehear.repository.NewsSourceRepository;

@Service
public class NewsSourceService {

    private final NewsSourceRepository newsSourceRepository;

    public NewsSourceService(NewsSourceRepository newsSourceRepository) {
        this.newsSourceRepository = newsSourceRepository;
    }

    public List<NewsSource> getAllActiveSources() {
        return newsSourceRepository.findAllActive();
    }

    public NewsSource getSourceById(Long id) {
        return newsSourceRepository.findById(id);
    }
}
