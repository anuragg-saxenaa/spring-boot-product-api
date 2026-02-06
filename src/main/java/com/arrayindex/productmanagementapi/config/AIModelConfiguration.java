package com.arrayindex.productmanagementapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
public class AIModelConfiguration {

    @Value("${ai.model.default:ollama}")
    private String defaultModel;

    @Value("${ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ai.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${ai.moonshot.api-key:}")
    private String moonshotApiKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HttpHeaders geminiHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiApiKey);
        return headers;
    }

    @Bean
    public HttpHeaders moonshotHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + moonshotApiKey);
        return headers;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public String getOllamaBaseUrl() {
        return ollamaBaseUrl;
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    public String getMoonshotApiKey() {
        return moonshotApiKey;
    }

    public boolean isGeminiConfigured() {
        return geminiApiKey != null && !geminiApiKey.isEmpty();
    }

    public boolean isMoonshotConfigured() {
        return moonshotApiKey != null && !moonshotApiKey.isEmpty();
    }
}