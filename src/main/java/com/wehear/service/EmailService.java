package com.wehear.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    private static final String RESEND_EMAILS_URL = "https://api.resend.com/emails";
    private static final String BRAND_NAME = "WeHear";
    private static final String BRAND_TAGLINE = "Cầu nối ngôn ngữ ký hiệu Việt Nam";

    private final RestTemplate restTemplate;

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.from.email}")
    private String fromEmail;

    @Value("${FRONTEND_URL:https://wehear.today}")
    private String frontendUrl;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(resendApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = Map.of(
                    "from", fromEmail,
                    "to", new String[] { to },
                    "subject", subject,
                    "html", htmlBody
            );

            restTemplate.postForEntity(RESEND_EMAILS_URL, new HttpEntity<>(payload, headers), String.class);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email từ WeHear", e);
        }
    }

    public String getWelcomeTemplate(String fullName) {
        return shell(
                "Chào mừng bạn đến với WeHear",
                "#2563eb",
                "<p style=\"font-size: 18px;\">Xin chào <strong>" + escape(fullName) + "</strong>,</p>" +
                "<p>Tài khoản WeHear của bạn đã được tạo thành công. Từ hôm nay, bạn có thể bắt đầu học, tra cứu và kết nối với cộng đồng ngôn ngữ ký hiệu Việt Nam.</p>" +
                "<p>WeHear rất vui được đồng hành cùng bạn trên hành trình giao tiếp dễ tiếp cận và gần gũi hơn.</p>" +
                button(frontendUrl, "Khám phá WeHear", "#2563eb") +
                "<p style=\"font-size: 14px; color: #64748b;\">Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email hoặc liên hệ bộ phận hỗ trợ của WeHear.</p>"
        );
    }

    public String getResetPasswordTemplate(String resetLink) {
        return shell(
                "Đặt lại mật khẩu WeHear",
                "#ef4444",
                "<p style=\"font-size: 18px;\">Xin chào,</p>" +
                "<p>WeHear nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>" +
                "<p>Vui lòng nhấn nút bên dưới để tạo mật khẩu mới. Liên kết này sẽ hết hạn sau <strong>15 phút</strong>.</p>" +
                button(resetLink, "Đặt lại mật khẩu", "#ef4444") +
                "<p style=\"font-size: 14px; color: #64748b;\">Nếu bạn không yêu cầu thao tác này, bạn có thể bỏ qua email. Mật khẩu hiện tại của bạn vẫn được giữ nguyên.</p>"
        );
    }

    public String getContributionApprovalTemplate(String fullName, String word) {
        return shell(
                "Đóng góp của bạn đã được duyệt",
                "#10b981",
                "<p style=\"font-size: 18px;\">Xin chào <strong>" + escape(fullName) + "</strong>,</p>" +
                "<p>WeHear đã duyệt đóng góp của bạn cho từ <strong>\"" + escape(word) + "\"</strong> và cập nhật vào hệ thống.</p>" +
                "<p>Cảm ơn bạn đã cùng xây dựng kho tri thức ngôn ngữ ký hiệu Việt Nam ngày càng phong phú và hữu ích hơn.</p>" +
                button(frontendUrl + "/dictionary", "Xem từ điển WeHear", "#10b981")
        );
    }

    public String getContributionRejectionTemplate(String fullName, String word, String reason) {
        String displayReason = reason != null && !reason.isBlank()
                ? escape(reason)
                : "WeHear chưa có ghi chú cụ thể cho lần từ chối này.";

        return shell(
                "Cập nhật về đóng góp của bạn",
                "#ef4444",
                "<p style=\"font-size: 18px;\">Xin chào <strong>" + escape(fullName) + "</strong>,</p>" +
                "<p>WeHear đã xem xét đóng góp của bạn cho từ <strong>\"" + escape(word) + "\"</strong>.</p>" +
                "<p>Rất tiếc, đóng góp này hiện chưa thể được duyệt.</p>" +
                "<div style=\"background-color: #fee2e2; border-left: 4px solid #ef4444; padding: 15px; margin: 20px 0;\">" +
                "  <p style=\"margin: 0; font-weight: bold; color: #991b1b;\">Lý do:</p>" +
                "  <p style=\"margin: 5px 0 0 0; color: #b91c1c;\">" + displayReason + "</p>" +
                "</div>" +
                "<p>Bạn có thể điều chỉnh nội dung và gửi lại khi sẵn sàng. WeHear luôn trân trọng sự đóng góp của bạn.</p>"
        );
    }

    public String getNewNewsNotificationTemplate(String fullName, String articleTitle, String articleSummary, String articleUrl) {
        return shell(
                "Tin mới từ WeHear",
                "#2563eb",
                "<p style=\"font-size: 18px;\">Xin chào <strong>" + escape(fullName) + "</strong>,</p>" +
                "<p>WeHear vừa cập nhật một bài viết mới có thể bạn sẽ quan tâm:</p>" +
                "<div style=\"background-color: #f1f5f9; padding: 20px; border-radius: 12px; margin: 20px 0;\">" +
                "  <h2 style=\"margin-top: 0; color: #1e293b; font-size: 20px;\">" + escape(articleTitle) + "</h2>" +
                "  <p style=\"color: #475569; font-size: 14px;\">" + escape(articleSummary) + "</p>" +
                "  <div style=\"text-align: right; margin-top: 15px;\">" +
                "    <a href=\"" + escapeAttribute(articleUrl) + "\" style=\"color: #2563eb; font-weight: bold; text-decoration: none;\">Đọc trên WeHear &rarr;</a>" +
                "  </div>" +
                "</div>" +
                "<p style=\"font-size: 14px; color: #64748b;\">Cảm ơn bạn đã theo dõi WeHear.</p>"
        );
    }

    private String shell(String title, String color, String body) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;\">" +
               "  <div style=\"background-color: " + color + "; padding: 30px; text-align: center;\">" +
               "    <h1 style=\"color: white; margin: 0; font-size: 26px;\">" + title + "</h1>" +
               "    <p style=\"color: rgba(255,255,255,0.86); margin: 10px 0 0; font-size: 14px;\">" + BRAND_TAGLINE + "</p>" +
               "  </div>" +
               "  <div style=\"padding: 30px; color: #1e293b; line-height: 1.6;\">" + body + "</div>" +
               "  <div style=\"background-color: #f8fafc; padding: 20px; text-align: center; color: #94a3b8; font-size: 12px;\">" +
               "    &copy; 2026 " + BRAND_NAME + ". All rights reserved." +
               "  </div>" +
               "</div>";
    }

    private String button(String href, String label, String color) {
        return "<div style=\"text-align: center; margin: 40px 0;\">" +
               "  <a href=\"" + escapeAttribute(href) + "\" style=\"background-color: " + color + "; color: white; padding: 14px 28px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block;\">" + label + "</a>" +
               "</div>";
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String escapeAttribute(String value) {
        return escape(value).replace("'", "&#39;");
    }
}
