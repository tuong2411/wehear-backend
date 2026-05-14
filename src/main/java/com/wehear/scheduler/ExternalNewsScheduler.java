package com.wehear.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wehear.service.ExternalNewsFetchService;

@Component
public class ExternalNewsScheduler {

    private final ExternalNewsFetchService externalNewsFetchService;

    public ExternalNewsScheduler(ExternalNewsFetchService externalNewsFetchService) {
        this.externalNewsFetchService = externalNewsFetchService;
    }

    @Scheduled(cron = "0 0 */6 * * *")
    public void autoFetchExternalNews() {
        System.out.println("========== AUTO FETCH EXTERNAL NEWS START ==========");

        int inserted = externalNewsFetchService.fetchAllSources();

        System.out.println("Auto fetch completed. Inserted: " + inserted + " article(s)");
        System.out.println("========== AUTO FETCH EXTERNAL NEWS END ==========");
    }
}