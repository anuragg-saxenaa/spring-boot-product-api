package com.arrayindex.productmanagementapi.controller;

import com.arrayindex.productmanagementapi.service.AIModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Model Controller", description = "AI-powered product enhancement APIs")
public class AIController {

    private final AIModelService aiModelService;

    @Autowired
    public AIController(AIModelService aiModelService) {
        this.aiModelService = aiModelService;
    }

    @Operation(summary = "Generate product description", description = "Generate a compelling product description using AI")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully generated description"),
        @ApiResponse(responseCode = "400", description = "Invalid input or model not available")
    })
    @PostMapping("/generate-description")
    public ResponseEntity<Map<String, String>> generateDescription(
            @Parameter(description = "Product name") @RequestParam String productName,
            @Parameter(description = "Product category") @RequestParam String category,
            @Parameter(description = "AI model to use (ollama, gemini, moonshot)") @RequestParam(required = false) String model) {
        
        String description = aiModelService.generateProductDescription(productName, category, model);
        return ResponseEntity.ok(Map.of("description", description));
    }

    @Operation(summary = "Suggest product categories", description = "Get AI-suggested categories for a product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully generated suggestions"),
        @ApiResponse(responseCode = "400", description = "Invalid input or model not available")
    })
    @PostMapping("/suggest-categories")
    public ResponseEntity<Map<String, List<String>>> suggestCategories(
            @Parameter(description = "Product name") @RequestParam String productName,
            @Parameter(description = "AI model to use (ollama, gemini, moonshot)") @RequestParam(required = false) String model) {
        
        List<String> categories = aiModelService.suggestCategories(productName, model);
        return ResponseEntity.ok(Map.of("categories", categories));
    }

    @Operation(summary = "Analyze product reviews", description = "Analyze sentiment and themes in product reviews")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully analyzed reviews"),
        @ApiResponse(responseCode = "400", description = "Invalid input or model not available")
    })
    @PostMapping("/analyze-reviews")
    public ResponseEntity<Map<String, String>> analyzeReviews(
            @Parameter(description = "List of product reviews") @RequestBody List<String> reviews,
            @Parameter(description = "AI model to use (ollama, gemini, moonshot)") @RequestParam(required = false) String model) {
        
        String analysis = aiModelService.analyzeReviewsSentiment(reviews, model);
        return ResponseEntity.ok(Map.of("analysis", analysis));
    }

    @Operation(summary = "Get available AI models", description = "List all available AI models")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved available models")
    })
    @GetMapping("/available-models")
    public ResponseEntity<Map<String, Object>> getAvailableModels() {
        List<String> models = aiModelService.getAvailableModels();
        String defaultModel = aiModelService.getDefaultModel();
        
        return ResponseEntity.ok(Map.of(
            "models", models,
            "defaultModel", defaultModel
        ));
    }

    @Operation(summary = "Health check for AI models", description = "Check connectivity and configuration of AI models")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Health check completed")
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
            "ollama", testOllamaConnection(),
            "gemini", testGeminiConnection(),
            "moonshot", testMoonshotConnection()
        );
        
        return ResponseEntity.ok(health);
    }

    private boolean testOllamaConnection() {
        try {
            String result = aiModelService.generateProductDescription("Test Product", "Electronics", "ollama");
            return !result.contains("Error");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean testGeminiConnection() {
        try {
            String result = aiModelService.generateProductDescription("Test Product", "Electronics", "gemini");
            return !result.contains("Error") && !result.contains("not configured");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean testMoonshotConnection() {
        try {
            String result = aiModelService.generateProductDescription("Test Product", "Electronics", "moonshot");
            return !result.contains("Error") && !result.contains("not configured");
        } catch (Exception e) {
            return false;
        }
    }
}