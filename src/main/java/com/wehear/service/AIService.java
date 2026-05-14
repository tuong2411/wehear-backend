package com.wehear.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wehear.model.QuizQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${google.ai.api.key}")
    private String apiKey;

    @Value("${google.ai.api.url}")
    private String primaryApiUrl;

    private static final int MAX_RETRIES = 3;

    public AIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Gợi ý danh sách từ vựng dựa trên topic với cơ chế Retry và Fallback thông minh.
     */
    public List<String> suggestWordsForTheme(String topic) {
        log.info("--- [AI PROCESS] Bắt đầu xử lý topic: '{}' ---", topic);
        
        String prompt = "Hãy liệt kê khoảng 15-20 từ vựng tiếng Việt phổ biến nhất liên quan mật thiết đến chủ đề: '" + topic + "'. " +
                "Chỉ trả về danh sách các từ, phân tách bằng dấu phẩy. Không kèm theo giải thích, không đánh số, không thêm văn bản thừa.";

        // Thử gọi AI với cơ chế Retry
        String response = callGeminiWithRetry(topic, prompt);
        
        List<String> resultWords;
        if (response != null && !response.isBlank()) {
            resultWords = Arrays.stream(response.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            log.info("[AI SUCCESS] Nhận được {} từ từ Gemini cho topic '{}'", resultWords.size(), topic);
        } else {
            // Nếu AI thất bại sau các lần retry, dùng Fallback theo đúng chủ đề
            resultWords = getFallbackWordsByTopic(topic);
            log.warn("[AI FALLBACK] Sử dụng danh sách dự phòng cho topic '{}': {}", topic, resultWords);
        }

        return resultWords;
    }

    /**
     * Gọi API Gemini với cơ chế Retry và Exponential Backoff cho lỗi 503.
     */
    private String callGeminiWithRetry(String topic, String prompt) {
        int attempt = 0;
        long waitTime = 1500; // Bắt đầu đợi 1.5s

        while (attempt < MAX_RETRIES) {
            try {
                attempt++;
                log.info("[AI ATTEMPT] Gọi Gemini API cho '{}' - Lần thử: {}/{}", topic, attempt, MAX_RETRIES);
                
                String fullUrl = primaryApiUrl + "?key=" + apiKey;
                Map<String, Object> requestBody = createGeminiRequest(prompt);
                
                ResponseEntity<Map> response = restTemplate.postForEntity(fullUrl, new HttpEntity<>(requestBody), Map.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    return extractTextFromGeminiResponse(response.getBody());
                }
            } catch (HttpServerErrorException.ServiceUnavailable e) {
                log.warn("[AI ERROR 503] Model đang quá tải khi xử lý '{}'. Thử lại sau {}ms...", topic, waitTime);
                if (attempt < MAX_RETRIES) {
                    performBackoff(waitTime);
                    waitTime *= 2; // Tăng thời gian đợi gấp đôi (Exponential Backoff)
                }
            } catch (Exception e) {
                log.error("[AI ERROR] Lỗi không xác định khi gọi AI cho '{}': {}", topic, e.getMessage());
                break; // Không retry cho các lỗi khác (400, 401, 403...)
            }
        }
        return null;
    }

    /**
     * Cung cấp danh sách từ vựng dự phòng (Fallback) chính xác theo chủ đề.
     */
    private List<String> getFallbackWordsByTopic(String topic) {
        String t = topic.toLowerCase();
        
        // Logic khớp từ khóa để trả về fallback phù hợp
        if (t.contains("gia đình") || t.contains("nhà") || t.contains("người thân")) {
            return Arrays.asList("Gia đình", "Bố", "Mẹ", "Anh", "Chị", "Em", "Ông", "Bà", "Con", "Cháu", "Vợ", "Chồng", "Yêu thương", "Sống", "Hạnh phúc");
        } 
        
        if (t.contains("trường") || t.contains("học") || t.contains("giáo dục")) {
            return Arrays.asList("Trường học", "Lớp học", "Thầy giáo", "Cô giáo", "Học sinh", "Sách", "Vở", "Bút", "Thước kẻ", "Bảng", "Bài tập", "Kiến thức", "Học tập");
        }
        
        if (t.contains("y tế") || t.contains("bệnh viện") || t.contains("đau") || t.contains("sức khỏe")) {
            return Arrays.asList("Bệnh viện", "Bác sĩ", "Y tá", "Thuốc", "Đau đầu", "Sốt", "Khám bệnh", "Cấp cứu", "Sức khỏe", "Bệnh nhân", "Uống thuốc", "Hiệu thuốc");
        }
        
        if (t.contains("giao tiếp") || t.contains("chào") || t.contains("hỏi")) {
            return Arrays.asList("Xin chào", "Cảm ơn", "Xin lỗi", "Tạm biệt", "Hẹn gặp lại", "Tên", "Tuổi", "Bạn", "Tôi", "Rất vui", "Khỏe không");
        }

        if (t.contains("ăn") || t.contains("uống") || t.contains("ẩm thực") || t.contains("món")) {
            return Arrays.asList("Ăn cơm", "Uống nước", "Ngon", "Đói", "Khát", "Nhà hàng", "Thực đơn", "Bữa sáng", "Bữa trưa", "Bữa tối", "Cơm", "Phở", "Bánh mì");
        }

        // Mặc định nếu không khớp từ khóa đặc biệt nào
        return Arrays.asList("Xin chào", "Cảm ơn", "Bạn", "Tôi", "Học tập", "Ngôn ngữ ký hiệu", "Việt Nam", "Wehear", "Giao tiếp");
    }

    private void performBackoff(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Retry backoff bị ngắt quãng: {}", e.getMessage());
        }
    }

    private Map<String, Object> createGeminiRequest(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", Collections.singletonList(part));
        requestBody.put("contents", Collections.singletonList(content));
        return requestBody;
    }

    private String extractTextFromGeminiResponse(Map body) {
        try {
            List candidates = (List) body.get("candidates");
            if (candidates == null || candidates.isEmpty()) return null;
            
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);
            
            return (String) firstPart.get("text");
        } catch (Exception e) {
            log.error("Lỗi khi trích xuất dữ liệu từ Gemini Response: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Tận dụng cơ chế Retry cho việc tạo Quiz trắc nghiệm.
     */
    public List<QuizQuestion> generateQuizQuestions(List<String> words) {
        if (words == null || words.isEmpty()) return new ArrayList<>();
        
        int count = words.size();
        String wordsStr = String.join(", ", words);
        String prompt = "Dựa trên danh sách từ vựng: [" + wordsStr + "]. " +
                "Hãy tạo đúng " + count + " câu hỏi trắc nghiệm tiếng Việt, mỗi câu hỏi tương ứng với một từ trong danh sách trên. " +
                "Trả về DUY NHẤT một JSON array. " +
                "Mỗi object có: questionText (VD: 'Ký hiệu này có nghĩa là gì?'), " +
                "questionType (luôn là 'MULTIPLE_CHOICE'), optionA, optionB, optionC, optionD, " +
                "và correctAnswer (PHẢI là nội dung văn bản của đáp án đúng, ví dụ: 'Gia đình', KHÔNG ĐƯỢC trả về 'A' hay 'B').";

        String aiResponse = callGeminiWithRetry("Quiz Generation", prompt);
        
        if (aiResponse != null) {
            try {
                String jsonStr = aiResponse.replaceAll("```json", "").replaceAll("```", "").trim();
                List<QuizQuestion> questions = objectMapper.readValue(jsonStr, new TypeReference<List<QuizQuestion>>() {});
                
                // Đảm bảo questionType không bao giờ null
                for (QuizQuestion q : questions) {
                    if (q.getQuestionType() == null || q.getQuestionType().isBlank()) {
                        q.setQuestionType("MULTIPLE_CHOICE");
                    }
                }
                return questions;
            } catch (Exception e) {
                log.error("[QUIZ ERROR] Lỗi parse JSON Quiz: {}", e.getMessage());
            }
        }
        return new ArrayList<>();
    }

    /**
     * Gợi ý các từ vựng phù hợp dựa trên ngữ cảnh bài học bằng cách mở rộng từ khóa.
     */
    public List<String> suggestRelevantSigns(String lessonContext, List<String> availableLabels) {
        // Thay vì gửi hàng ngàn từ vào Prompt (gây lỗi 503), ta yêu cầu AI tự tạo danh sách liên quan.
        String prompt = "Dựa trên tiêu đề và mô tả bài học: '" + lessonContext + "'. " +
                "Hãy liệt kê khoảng 30-40 từ vựng tiếng Việt phổ biến và liên quan mật thiết nhất đến chủ đề này. " +
                "Chỉ trả về danh sách các từ, phân tách bằng dấu phẩy. Không giải thích, không đánh số.";

        String aiResponse = callGeminiWithRetry("Sign Expansion", prompt);
        
        if (aiResponse != null && !aiResponse.isBlank()) {
            return Arrays.stream(aiResponse.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        
        // Nếu AI thất bại, sử dụng fallback dựa trên bộ từ vựng theo chủ đề có sẵn
        log.warn("[AI FALLBACK] AI Expansion failed, using topic fallback for: {}", lessonContext);
        return getFallbackWordsByTopic(lessonContext);
    }
}
