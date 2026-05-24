package com.wehear.service;

import com.rometools.rome.feed.module.Module;
import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.modules.mediarss.types.MediaContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.wehear.model.ExternalNewsArticle;
import com.wehear.model.NewsSource;
import com.wehear.repository.ExternalNewsRepository;
import com.wehear.repository.NewsSourceRepository;
import com.wehear.repository.UserRepository;
import com.wehear.util.HtmlUtil;
import com.wehear.util.NewsKeywordMatcher;
import com.wehear.util.SlugUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExternalNewsFetchService {

    private final NewsSourceRepository newsSourceRepository;
    private final ExternalNewsRepository externalNewsRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Value("${FRONTEND_URL:https://wehear.today}")
    private String frontendUrl;

    public ExternalNewsFetchService(
            NewsSourceRepository newsSourceRepository,
            ExternalNewsRepository externalNewsRepository,
            EmailService emailService,
            UserRepository userRepository) {
        this.newsSourceRepository = newsSourceRepository;
        this.externalNewsRepository = externalNewsRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    public int fetchAllSources() {
        List<NewsSource> sources = newsSourceRepository.findAllActive();
        int totalInserted = 0;
        for (NewsSource source : sources) {
            if (source.getRssUrl() != null && !source.getRssUrl().isBlank()) {
                totalInserted += fetchFromRss(source);
            }
        }
        return totalInserted;
    }

    private int fetchFromRss(NewsSource source) {
        int insertedCount = 0;
        try {
            System.out.println("--- FETCHING: " + source.getSourceName() + " ---");
            URL rssUrl = new URL(source.getRssUrl());
            HttpURLConnection conn = (HttpURLConnection) rssUrl.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            // SỬ DỤNG XmlReader: Tự động xử lý Encoding và BOM (Sửa lỗi prolog ở VietnamPlus)
            XmlReader reader = new XmlReader(conn);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(reader);
            
            List<SyndEntry> entries = feed.getEntries();
            for (SyndEntry entry : entries) {
                String articleUrl = entry.getLink();
                if (articleUrl == null || externalNewsRepository.existsByArticleUrl(articleUrl)) continue;

                String title = entry.getTitle();
                String rawDesc = entry.getDescription() != null ? entry.getDescription().getValue() : "";
                
                // Cố gắng lấy ảnh từ RSS, nếu không có thì đọc từ link bài báo
                String thumbnailUrl = findThumbnailFromRss(entry, rawDesc, articleUrl);
                
                String summary = HtmlUtil.stripHtml(rawDesc);
                NewsKeywordMatcher.MatchResult matchResult = NewsKeywordMatcher.evaluate(title, summary);

                if (!matchResult.isRelevant()) continue;

                // Nếu không có ảnh thật (hoặc ảnh Google bị skip), dùng ảnh mặc định
                if (thumbnailUrl == null) {
                    String tags = matchResult.getTags();
                    List<String> tagList = (tags == null || tags.isBlank()) 
                            ? Collections.emptyList() 
                            : Arrays.asList(tags.split(","));
                    thumbnailUrl = getDefaultImage(tagList);
                }

                ExternalNewsArticle article = new ExternalNewsArticle();
                article.setSourceId(source.getId());
                article.setTitle(title);
                article.setSlug(SlugUtil.toSlug(title) + "-" + System.currentTimeMillis());
                article.setSummary(summary);
                article.setArticleUrl(articleUrl);
                article.setThumbnailUrl(thumbnailUrl); 
                Timestamp pubTimestamp = entry.getPublishedDate() != null 
                    ? new Timestamp(entry.getPublishedDate().getTime()) 
                    : new Timestamp(System.currentTimeMillis());
                article.setPublishedAt(pubTimestamp.toLocalDateTime());
                article.setCategory("news");
                article.setTags(matchResult.getTags());
                article.setLanguageCode("vi");
                article.setContentType("EXTERNAL");
                article.setStatus("ACTIVE");
                article.setRelevanceScore(matchResult.getScore());

                if (externalNewsRepository.insert(article) > 0) {
                    insertedCount++;
                    System.out.println("[MATCHED] Source: " + source.getSourceName());
                    System.out.println("  Title: " + title);
                    System.out.println("  URL: " + articleUrl);
                    System.out.println("  IMG: " + thumbnailUrl);

                    // Gửi thông báo cho toàn bộ người dùng nếu bài báo mới (trong vòng 24h qua)
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();
                    if (article.getPublishedAt() != null && article.getPublishedAt().isAfter(now.minusHours(48))) {
                        sendNewsNotificationToAllUsers(article);
                    } else {
                        System.out.println("  [SKIP NOTIFY] Article is too old: " + article.getPublishedAt());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR " + source.getSourceName() + ": " + e.getMessage());
        }
        return insertedCount;
    }

    private void sendNewsNotificationToAllUsers(ExternalNewsArticle article) {
        try {
            List<com.wehear.model.User> users = userRepository.findAll();
            String articleFrontendUrl = frontendUrl + "/news/" + article.getSlug();
            
            for (com.wehear.model.User user : users) {
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    String html = emailService.getNewNewsNotificationTemplate(
                        user.getFullName(), 
                        article.getTitle(), 
                        article.getSummary(), 
                        articleFrontendUrl
                    );
                    emailService.sendHtmlEmail(user.getEmail(), "Tin mới từ WeHear: " + article.getTitle(), html);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to send news notifications: " + e.getMessage());
        }
    }

    private String getDefaultImage(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "/images/default/news.svg";
        }
        // Ưu tiên theo thứ tự quan trọng
        if (tags.contains("ngon_ngu_ky_hieu")) {
            return "/images/default/sign-language.svg";
        } else if (tags.contains("ai")) {
            return "/images/default/ai.svg";
        } else if (tags.contains("giao_duc")) {
            return "/images/default/education.svg";
        }
        return "/images/default/news.svg";
    }

    private String findThumbnailFromRss(SyndEntry entry, String rawDescription, String articleUrl) {
        // 1. Media RSS
        String[] mrssUris = {MediaEntryModule.URI, "http://search.yahoo.com/mrss/"};
        for (String uri : mrssUris) {
            try {
                Module mediaModule = entry.getModule(uri);
                if (mediaModule instanceof MediaEntryModule) {
                    MediaEntryModule me = (MediaEntryModule) mediaModule;
                    if (me.getMediaContents() != null && me.getMediaContents().length > 0) {
                        MediaContent firstContent = me.getMediaContents()[0];
                        if (firstContent != null && firstContent.getReference() != null) {
                            String url = firstContent.getReference().toString();
                            if (isUsableImageUrl(url)) return url;
                            else if (isGoogleUrl(url)) System.out.println("  [SKIP] Google MediaRSS image: " + url);
                        }
                    }
                    if (me.getMetadata() != null && me.getMetadata().getThumbnail() != null && me.getMetadata().getThumbnail().length > 0) {
                        if (me.getMetadata().getThumbnail()[0] != null && me.getMetadata().getThumbnail()[0].getUrl() != null) {
                            String url = me.getMetadata().getThumbnail()[0].getUrl().toString();
                            if (isUsableImageUrl(url)) return url;
                            else if (isGoogleUrl(url)) System.out.println("  [SKIP] Google MediaRSS thumbnail: " + url);
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        // 2. Enclosures
        if (entry.getEnclosures() != null && !entry.getEnclosures().isEmpty()) {
            for (com.rometools.rome.feed.synd.SyndEnclosure enclosure : entry.getEnclosures()) {
                if (enclosure != null && enclosure.getType() != null && enclosure.getType().contains("image")) {
                    String url = enclosure.getUrl();
                    if (isUsableImageUrl(url)) return url;
                    else if (isGoogleUrl(url)) System.out.println("  [SKIP] Google Enclosure image: " + url);
                }
            }
        }

        // 3. Quét Jsoup trong Description
        String img = HtmlUtil.extractFirstImageUrl(rawDescription);
        if (isUsableImageUrl(img)) return img;
        else if (isGoogleUrl(img)) System.out.println("  [SKIP] Google image in description: " + img);

        // 4. Regex link ảnh thô
        Pattern p = Pattern.compile("(https?://\\S+?\\.(?:jpg|jpeg|png|webp|gif)(\\?\\S*)?)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(rawDescription);
        while (m.find()) {
            String url = m.group(1);
            if (isUsableImageUrl(url)) return url;
            else if (isGoogleUrl(url)) System.out.println("  [SKIP] Google regex image: " + url);
        }

        // 5. Fallback: Đọc trực tiếp từ link bài báo
        return extractOgImageFromArticle(articleUrl);
    }

    private String extractOgImageFromArticle(String articleUrl) {
        if (articleUrl == null || articleUrl.isBlank()) return null;
        // KHÔNG scrape link redirect của Google News để lấy ảnh vì thường chỉ ra ảnh logo Google
        if (articleUrl.contains("news.google.com")) return null;

        try {
            Document doc = Jsoup.connect(articleUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .timeout(5000)
                    .get();

            // 1. Open Graph image
            String ogImage = doc.select("meta[property=og:image]").attr("content");
            if (isUsableImageUrl(ogImage)) return ogImage;

            // 2. Twitter image
            String twitterImage = doc.select("meta[name=twitter:image]").attr("content");
            if (isUsableImageUrl(twitterImage)) return twitterImage;

            // 3. First image in article
            Element firstImg = doc.select("img").first();
            if (firstImg != null) {
                String imgUrl = firstImg.absUrl("src");
                if (isUsableImageUrl(imgUrl)) return imgUrl;
            }
        } catch (Exception e) {
            System.err.println("FALLBACK IMG ERROR: " + articleUrl + " - " + e.getMessage());
        }
        return null;
    }

    private boolean isGoogleUrl(String url) {
        if (url == null) return false;
        String lower = url.toLowerCase();
        return lower.contains("googleusercontent.com") || 
               lower.contains("gstatic.com") || 
               lower.contains("news.google.com");
    }

    private boolean isUsableImageUrl(String url) {
        if (url == null || url.isBlank()) return false;
        if (!url.startsWith("http://") && !url.startsWith("https://")) return false;

        if (isGoogleUrl(url)) return false;

        String lowerUrl = url.toLowerCase();
        // Loại bỏ các ảnh là logo hoặc icon
        if (lowerUrl.contains("logo") || lowerUrl.contains("icon")) {
            return false;
        }

        return true;
    }
}
