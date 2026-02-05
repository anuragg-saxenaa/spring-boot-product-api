package com.arrayindex.productmanagementapi.service;

import com.arrayindex.productmanagementapi.config.AIModelConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AIModelService {

    private final AIModelConfiguration aiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public AIModelService(AIModelConfiguration aiConfig, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.aiConfig = aiConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Generate product description using the specified AI model
     * @param productName Name of the product
     * @param category Product category
     * @param model AI model to use (ollama, gemini, moonshot)
     * @return Generated description
     */
    public String generateProductDescription(String productName, String category, String model) {
        if (model == null) {
            model = aiConfig.getDefaultModel();
        }

        String prompt = "Generate a compelling product description for a " + category + " named '" + productName + 
                       "'. The description should be professional, engaging, and highlight key features. Keep it under 200 words.";

        switch (model.toLowerCase()) {
            case "gemini":
                return callGeminiAPI(prompt);
            case "moonshot":
                return callMoonshotAPI(prompt);
            case "ollama":
            default:
                return callOllamaAPI(prompt);
        }
    }

    /**
     * Generate product category suggestions using AI
     * @param productName Name of the product
     * @param model AI model to use
     * @return List of suggested categories
     */
    public List<String> suggestCategories(String productName, String model) {
        if (model == null) {
            model = aiConfig.getDefaultModel();
        }

        String prompt = "Suggest 3-5 appropriate categories for a product named '" + productName + 
                       "'. Return only the category names, separated by commas.";

        String response;
        switch (model.toLowerCase()) {
            case "gemini":
                response = callGeminiAPI(prompt);
                break;
            case "moonshot":
                response = callMoonshotAPI(prompt);
                break;
            case "ollama":
            default:
                response = callOllamaAPI(prompt);
                break;
        }

        return Arrays.asList(response.split(","));
    }

    /**
     * Analyze product reviews sentiment using AI
     * @param reviews List of product reviews
     * @param model AI model to use
     * @return Sentiment analysis result
     */
    public String analyzeReviewsSentiment(List<String> reviews, String model) {
        if (model == null) {
            model = aiConfig.getDefaultModel();
        }

        String reviewsText = String.join("\n", reviews);
        String prompt = "Analyze the sentiment of these product reviews and provide a summary:\n\n" + reviewsText + 
                       "\n\nProvide: 1) Overall sentiment (positive/negative/neutral), 2) Key themes, 3) Suggestions for improvement";

        switch (model.toLowerCase()) {
            case "gemini":
                return callGeminiAPI(prompt);
            case "moonshot":
                return callMoonshotAPI(prompt);
            case "ollama":
            default:
                return callOllamaAPI(prompt);
        }
    }

    // Private methods for each AI provider

    private String callGeminiAPI(String prompt) {
        if (!aiConfig.isGeminiConfigured()) {
            return "Gemini API not configured. Please set ai.gemini.api-key in application.properties";
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + aiConfig.getGeminiApiKey();
            
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            content.put("parts", Arrays.asList(part));
            requestBody.put("contents", Arrays.asList(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> contentData = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, String>> parts = (List<Map<String, String>>) contentData.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        return parts.get(0).get("text");
                    }
                }
            }
            return "Error: Invalid response from Gemini API";
        } catch (Exception e) {
            return "Error calling Gemini API: " + e.getMessage();
        }
    }

    private String callMoonshotAPI(String prompt) {
        if (!aiConfig.isMoonshotConfigured()) {
            return "Moonshot API not configured. Please set ai.moonshot.api-key in application.properties";
        }

        try {
            String url = "https://api.moonshot.cn/v1/chat/completions";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "moonshot-v1-8k");
            
            List<Map<String, String>> messages = Arrays.asList(
                Map.of("role", "user", "content", prompt)
            );
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiConfig.getMoonshotApiKey());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            return "Error: Invalid response from Moonshot API";
        } catch (Exception e) {
            return "Error calling Moonshot API: " + e.getMessage();
        }
    }

    private String callOllamaAPI(String prompt) {
        try {
            String url = aiConfig.getOllamaBaseUrl() + "/api/generate";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama3.1:8b");
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return (String) responseBody.get("response");
            }
            return "Error: Invalid response from Ollama API";
        } catch (Exception e) {
            return "Error calling Ollama API: " + e.getMessage();
        }
    }

    /**
     * Get list of available AI models
     */
    public List<String> getAvailableModels() {
        List<String> models = new ArrayList<>();
        models.add("ollama"); // Always available as default
        
        if (aiConfig.isGeminiConfigured()) {
            models.add("gemini");
        }
        
        if (aiConfig.isMoonshotConfigured()) {
            models.add("moonshot");
        }
        
        return models;
    }

    /**
     * Get the current default model
     */
    public String getDefaultModel() {
        return aiConfig.getDefaultModel();
    }
}