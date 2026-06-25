package com.wehear.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class TextToSpeechService {

    private static final int MAX_TEXT_LENGTH = 1000;
    private static final int AUDIO_RETRY_COUNT = 8;
    private static final long AUDIO_RETRY_DELAY_MS = 700;

    private final RestTemplate restTemplate;

    @Value("${fpt.ai.tts.api-key:}")
    private String apiKey;

    @Value("${fpt.ai.tts.url:https://api.fpt.ai/hmi/tts/v5}")
    private String ttsUrl;

    @Value("${fpt.ai.tts.voice:banmai}")
    private String voice;

    @Value("${fpt.ai.tts.speed:0}")
    private String speed;

    public TextToSpeechService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public byte[] synthesize(String text) {
        String normalizedText = normalizeText(text);

        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Chưa cấu hình FPT_AI_API_KEY.");
        }

        String audioUrl = requestAudioUrl(normalizedText);
        return downloadAudioWhenReady(audioUrl);
    }

    private String normalizeText(String text) {
        String normalized = text == null ? "" : text.trim().replaceAll("\\s+", " ");

        if (normalized.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập nội dung cần đọc.");
        }

        if (normalized.length() > MAX_TEXT_LENGTH) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nội dung đọc không được vượt quá " + MAX_TEXT_LENGTH + " ký tự."
            );
        }

        return normalized;
    }

    @SuppressWarnings("unchecked")
    private String requestAudioUrl(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        headers.set("api-key", apiKey);
        headers.set("voice", voice);
        headers.set("speed", speed);

        try {
            byte[] utf8Body = text.getBytes(StandardCharsets.UTF_8);
            ResponseEntity<Map> response = restTemplate.exchange(
                    ttsUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(utf8Body, headers),
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            Object asyncUrl = body == null ? null : body.get("async");
            if (!(asyncUrl instanceof String url) || url.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "FPT.AI chưa trả về đường dẫn audio.");
            }

            return url;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Không thể gọi FPT.AI TTS.", e);
        }
    }

    private byte[] downloadAudioWhenReady(String audioUrl) {
        for (int attempt = 0; attempt < AUDIO_RETRY_COUNT; attempt++) {
            try {
                ResponseEntity<byte[]> response = restTemplate.exchange(
                        audioUrl,
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        byte[].class
                );

                byte[] audio = response.getBody();
                if (response.getStatusCode().is2xxSuccessful() && audio != null && audio.length > 0) {
                    return audio;
                }
            } catch (RestClientException ignored) {
                // FPT.AI creates the audio asynchronously, so the URL can be unavailable for a short time.
            }

            waitBeforeRetry();
        }

        throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "FPT.AI chưa tạo xong audio. Vui lòng thử lại.");
    }

    private void waitBeforeRetry() {
        try {
            Thread.sleep(AUDIO_RETRY_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Quá trình tạo audio bị gián đoạn.", e);
        }
    }
}
