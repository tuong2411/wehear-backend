package com.wehear.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class TextToSpeechService {

    private static final int MAX_TEXT_LENGTH = 1000;

    private final RestTemplate restTemplate;

    @Value("${google.cloud.tts.api-key:}")
    private String apiKey;

    @Value("${google.cloud.tts.url:https://texttospeech.googleapis.com/v1/text:synthesize}")
    private String ttsUrl;

    @Value("${google.cloud.tts.language-code:vi-VN}")
    private String languageCode;

    @Value("${google.cloud.tts.voice-name:vi-VN-Neural2-A}")
    private String voice;

    @Value("${google.cloud.tts.speaking-rate:1.0}")
    private double speakingRate;

    public TextToSpeechService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public byte[] synthesize(String text) {
        String normalizedText = normalizeText(text);

        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Chưa cấu hình GOOGLE_CLOUD_TTS_API_KEY.");
        }

        return synthesizeWithGoogle(normalizedText);
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
    private byte[] synthesizeWithGoogle(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    ttsUrl + "?key=" + apiKey,
                    new HttpEntity<>(buildRequestBody(text), headers),
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            Object audioContent = body == null ? null : body.get("audioContent");
            if (!(audioContent instanceof String encodedAudio) || encodedAudio.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Google Cloud TTS chưa trả về audio.");
            }

            return Base64.getDecoder().decode(encodedAudio);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Audio Google Cloud TTS trả về không hợp lệ.", e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Không thể gọi Google Cloud TTS.", e);
        }
    }

    private Map<String, Object> buildRequestBody(String text) {
        Map<String, Object> input = new HashMap<>();
        input.put("text", text);

        Map<String, Object> voiceConfig = new HashMap<>();
        voiceConfig.put("languageCode", languageCode);
        voiceConfig.put("name", voice);

        Map<String, Object> audioConfig = new HashMap<>();
        audioConfig.put("audioEncoding", "MP3");
        audioConfig.put("speakingRate", speakingRate);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("input", input);
        requestBody.put("voice", voiceConfig);
        requestBody.put("audioConfig", audioConfig);

        return requestBody;
    }
}
