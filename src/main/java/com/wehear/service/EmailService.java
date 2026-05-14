package com.wehear.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("wehear.support@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public String getWelcomeTemplate(String fullName) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;\">" +
               "  <div style=\"background-color: #2563eb; padding: 30px; text-align: center;\">" +
               "    <h1 style=\"color: white; margin: 0; font-size: 28px;\">Chào mừng bạn đến với Wehear!</h1>" +
               "  </div>" +
               "  <div style=\"padding: 30px; color: #1e293b; line-height: 1.6;\">" +
               "    <p style=\"font-size: 18px;\">Xin chào <strong>" + fullName + "</strong>,</p>" +
               "    <p>Chúc mừng bạn đã đăng ký tài khoản thành công tại <strong>Wehear - Cầu nối ngôn ngữ ký hiệu Việt Nam</strong>.</p>" +
               "    <p>Chúng tôi rất vui mừng được đồng hành cùng bạn trong hành trình xóa bỏ rào cản ngôn ngữ và kết nối cộng đồng.</p>" +
               "    <div style=\"text-align: center; margin: 40px 0;\">" +
               "      <a href=\"http://localhost:3001\" style=\"background-color: #2563eb; color: white; padding: 14px 28px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block;\">Bắt đầu khám phá ngay</a>" +
               "    </div>" +
               "    <p style=\"font-size: 14px; color: #64748b;\">Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email hoặc liên hệ với bộ phận hỗ trợ của chúng tôi.</p>" +
               "  </div>" +
               "  <div style=\"background-color: #f8fafc; padding: 20px; text-align: center; color: #94a3b8; font-size: 12px;\">" +
               "    &copy; 2026 Wehear Project. All rights reserved." +
               "  </div>" +
               "</div>";
    }

    public String getResetPasswordTemplate(String resetLink) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;\">" +
               "  <div style=\"background-color: #2563eb; padding: 30px; text-align: center;\">" +
               "    <h1 style=\"color: white; margin: 0; font-size: 28px;\">Đặt lại mật khẩu</h1>" +
               "  </div>" +
               "  <div style=\"padding: 30px; color: #1e293b; line-height: 1.6;\">" +
               "    <p style=\"font-size: 18px;\">Xin chào,</p>" +
               "    <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản Wehear của bạn.</p>" +
               "    <p>Vui lòng nhấn vào nút bên dưới để tiến hành thay đổi mật khẩu. Link này sẽ <strong>hết hạn trong 15 phút</strong>.</p>" +
               "    <div style=\"text-align: center; margin: 40px 0;\">" +
               "      <a href=\"" + resetLink + "\" style=\"background-color: #ef4444; color: white; padding: 14px 28px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block;\">Đặt lại mật khẩu ngay</a>" +
               "    </div>" +
               "    <p style=\"font-size: 14px; color: #64748b;\">Nếu bạn không yêu cầu đổi mật khẩu, bạn có thể yên tâm bỏ qua email này. Mật khẩu của bạn vẫn an toàn.</p>" +
               "  </div>" +
               "  <div style=\"background-color: #f8fafc; padding: 20px; text-align: center; color: #94a3b8; font-size: 12px;\">" +
               "    &copy; 2026 Wehear Project. All rights reserved." +
               "  </div>" +
               "</div>";
    }

    public String getContributionApprovalTemplate(String fullName, String word) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;\">" +
               "  <div style=\"background-color: #10b981; padding: 30px; text-align: center;\">" +
               "    <h1 style=\"color: white; margin: 0; font-size: 28px;\">Đóng góp của bạn đã được duyệt!</h1>" +
               "  </div>" +
               "  <div style=\"padding: 30px; color: #1e293b; line-height: 1.6;\">" +
               "    <p style=\"font-size: 18px;\">Xin chào <strong>" + fullName + "</strong>,</p>" +
               "    <p>Chúng tôi rất vui mừng thông báo rằng đóng góp của bạn cho từ: <strong>\"" + word + "\"</strong> đã được ban quản trị chấp nhận và cập nhật vào hệ thống.</p>" +
               "    <p>Cảm ơn bạn đã chung tay xây dựng cộng đồng Wehear ngày càng phát triển. Những đóng góp của bạn là vô cùng quý giá đối với cộng đồng người khiếm thính.</p>" +
               "    <div style=\"text-align: center; margin: 40px 0;\">" +
               "      <a href=\"http://localhost:3001/dictionary\" style=\"background-color: #10b981; color: white; padding: 14px 28px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block;\">Xem từ điển ngay</a>" +
               "    </div>" +
               "  </div>" +
               "  <div style=\"background-color: #f8fafc; padding: 20px; text-align: center; color: #94a3b8; font-size: 12px;\">" +
               "    &copy; 2026 Wehear Project. All rights reserved." +
               "  </div>" +
               "</div>";
    }

    public String getContributionRejectionTemplate(String fullName, String word, String reason) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;\">" +
               "  <div style=\"background-color: #ef4444; padding: 30px; text-align: center;\">" +
               "    <h1 style=\"color: white; margin: 0; font-size: 28px;\">Thông báo về đóng góp của bạn</h1>" +
               "  </div>" +
               "  <div style=\"padding: 30px; color: #1e293b; line-height: 1.6;\">" +
               "    <p style=\"font-size: 18px;\">Xin chào <strong>" + fullName + "</strong>,</p>" +
               "    <p>Chúng tôi đã xem xét đóng góp của bạn cho từ: <strong>\"" + word + "\"</strong>.</p>" +
               "    <p>Rất tiếc, chúng tôi chưa thể chấp nhận đóng góp này vào lúc này.</p>" +
               "    <div style=\"background-color: #fee2e2; border-left: 4px solid #ef4444; padding: 15px; margin: 20px 0;\">" +
               "      <p style=\"margin: 0; font-weight: bold; color: #991b1b;\">Lý do từ chối:</p>" +
               "      <p style=\"margin: 5px 0 0 0; color: #b91c1c;\">" + (reason != null && !reason.isEmpty() ? reason : "Không có lý do cụ thể được cung cấp.") + "</p>" +
               "    </div>" +
               "    <p>Bạn có thể điều chỉnh lại đóng góp của mình và gửi lại cho chúng tôi. Chúng tôi luôn trân trọng sự nhiệt tình của bạn.</p>" +
               "  </div>" +
               "  <div style=\"background-color: #f8fafc; padding: 20px; text-align: center; color: #94a3b8; font-size: 12px;\">" +
               "    &copy; 2026 Wehear Project. All rights reserved." +
               "  </div>" +
               "</div>";
    }

    public String getNewNewsNotificationTemplate(String fullName, String articleTitle, String articleSummary, String articleUrl) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;\">" +
               "  <div style=\"background-color: #2563eb; padding: 30px; text-align: center;\">" +
               "    <h1 style=\"color: white; margin: 0; font-size: 24px;\">Tin tức mới từ Wehear</h1>" +
               "  </div>" +
               "  <div style=\"padding: 30px; color: #1e293b; line-height: 1.6;\">" +
               "    <p style=\"font-size: 18px;\">Xin chào <strong>" + fullName + "</strong>,</p>" +
               "    <p>Chúng tôi vừa cập nhật một tin tức mới có thể bạn sẽ quan tâm:</p>" +
               "    <div style=\"background-color: #f1f5f9; padding: 20px; border-radius: 12px; margin: 20px 0;\">" +
               "      <h2 style=\"margin-top: 0; color: #1e293b; font-size: 20px;\">" + articleTitle + "</h2>" +
               "      <p style=\"color: #475569; font-size: 14px;\">" + articleSummary + "</p>" +
               "      <div style=\"text-align: right; margin-top: 15px;\">" +
               "        <a href=\"" + articleUrl + "\" style=\"color: #2563eb; font-weight: bold; text-decoration: none;\">Đọc tiếp &rarr;</a>" +
               "      </div>" +
               "    </div>" +
               "    <p style=\"font-size: 14px; color: #64748b;\">Chúc bạn một ngày tốt lành!</p>" +
               "  </div>" +
               "  <div style=\"background-color: #f8fafc; padding: 20px; text-align: center; color: #94a3b8; font-size: 12px;\">" +
               "    &copy; 2026 Wehear Project. All rights reserved." +
               "  </div>" +
               "</div>";
    }
}
