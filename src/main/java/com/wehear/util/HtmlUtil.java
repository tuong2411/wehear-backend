package com.wehear.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlUtil {

    public static String stripHtml(String html) {
        if (html == null || html.isBlank()) return "";
        return Jsoup.parse(html).text().trim();
    }

    public static String extractFirstImageUrl(String html) {
        if (html == null || html.isBlank()) return null;

        try {
            Document doc = Jsoup.parse(html);

            // 1. img[src]
            Elements imgs = doc.select("img");
            for (Element img : imgs) {
                String src = normalizeImageUrl(img.attr("src"));
                if (isValidCandidate(src)) return src;

                String dataSrc = normalizeImageUrl(img.attr("data-src"));
                if (isValidCandidate(dataSrc)) return dataSrc;

                String dataOriginal = normalizeImageUrl(img.attr("data-original"));
                if (isValidCandidate(dataOriginal)) return dataOriginal;

                String srcset = extractFirstFromSrcset(img.attr("srcset"));
                if (isValidCandidate(srcset)) return srcset;

                String dataSrcset = extractFirstFromSrcset(img.attr("data-srcset"));
                if (isValidCandidate(dataSrcset)) return dataSrcset;
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String extractFirstFromSrcset(String srcset) {
        if (srcset == null || srcset.isBlank()) return null;

        String[] candidates = srcset.split(",");
        if (candidates.length == 0) return null;

        String first = candidates[0].trim();
        if (first.isBlank()) return null;

        String[] parts = first.split("\\s+");
        return parts.length > 0 ? normalizeImageUrl(parts[0]) : null;
    }

    private static String normalizeImageUrl(String url) {
        if (url == null) return null;
        String trimmed = url.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean isValidCandidate(String url) {
        if (url == null || url.isBlank()) return false;

        String lower = url.toLowerCase();

        if (lower.startsWith("data:")) return false;
        if (!(lower.startsWith("http://") || lower.startsWith("https://"))) return false;

        return true;
    }
}