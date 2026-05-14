package com.wehear.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NewsKeywordMatcher {

    public static class MatchResult {
        private final BigDecimal score;
        private final String tags;
        private final boolean relevant;

        public MatchResult(BigDecimal score, String tags, boolean relevant) {
            this.score = score;
            this.tags = tags;
            this.relevant = relevant;
        }

        public BigDecimal getScore() {
            return score;
        }

        public String getTags() {
            return tags;
        }

        public boolean isRelevant() {
            return relevant;
        }
    }

    public static MatchResult evaluate(String title, String summary) {
        String content = ((title == null ? "" : title) + " " + (summary == null ? "" : summary)).toLowerCase();

        double score = 0.0;
        Set<String> matchedTags = new LinkedHashSet<>();

        // NHÓM 1: TRỌNG TÂM (Ưu tiên tuyệt đối - 10 điểm)
        score += match(content, matchedTags, 10.0, "ngôn ngữ ký hiệu", "ngon_ngu_ky_hieu");
        score += match(content, matchedTags, 10.0, "sign language", "sign_language");
        score += match(content, matchedTags, 10.0, "phiên dịch ký hiệu", "phien_dich_ky_hieu");
        score += match(content, matchedTags, 10.0, "ký hiệu bàn tay", "ky_hieu_ban_tay");
        score += match(content, matchedTags, 10.0, "ngôn ngữ đôi tay", "ngon_ngu_doi_tay");
        score += match(content, matchedTags, 10.0, "thủ ngữ", "thu_ngu");

        // NHÓM 2: NGỮ CẢNH CỘNG ĐỒNG (5 điểm)
        score += match(content, matchedTags, 5.0, "khiếm thính", "khiem_thinh");
        score += match(content, matchedTags, 5.0, "người điếc", "nguoi_diec");
        score += match(content, matchedTags, 5.0, "cộng đồng người điếc", "cong_dong_nguoi_diec");
        score += match(content, matchedTags, 5.0, "văn hóa điếc", "van_hoa_diec");
        score += match(content, matchedTags, 5.0, "trợ thính", "tro_thinh");
        score += match(content, matchedTags, 5.0, "điếc bẩm sinh", "diec_bam_sinh");
        score += match(content, matchedTags, 5.0, "deaf", "deaf");
        score += match(content, matchedTags, 5.0, "hearing impaired", "hearing_impaired");

        // NHÓM 3: CÔNG NGHỆ & HÒA NHẬP (Nếu đi kèm nhóm 1 hoặc 2 mới có giá trị)
        score += match(content, matchedTags, 3.0, "vrs", "vrs");
        score += match(content, matchedTags, 3.0, "video relay service", "vrs");
        score += match(content, matchedTags, 3.0, "chuyển đổi ngôn ngữ ký hiệu", "chuyen_doi_ky_hieu");
        score += match(content, matchedTags, 2.0, "giao tiếp", "giao_tiep");
        score += match(content, matchedTags, 2.0, "hòa nhập", "hoa_nhap");
        score += match(content, matchedTags, 2.0, "hỗ trợ", "ho_tro");

        // ĐIỀU KIỆN QUYẾT ĐỊNH: Phải đạt ít nhất 10 điểm
        // (Ví dụ: Có từ "ngôn ngữ ký hiệu" hoặc có 2 từ trong nhóm "khiếm thính", "người điếc")
        boolean relevant = score >= 10.0;

        return new MatchResult(
                BigDecimal.valueOf(score),
                String.join(",", matchedTags),
                relevant
        );
    }

    private static double match(String content, Set<String> matchedTags, double point, String keyword, String tag) {
        if (content.contains(keyword.toLowerCase())) {
            matchedTags.add(tag);
            return point;
        }
        return 0.0;
    }
}