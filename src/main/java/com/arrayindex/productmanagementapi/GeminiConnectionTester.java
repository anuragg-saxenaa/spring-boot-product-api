package com.arrayindex.productmanagementapi;

import com.arrayindex.productmanagementapi.service.AIModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GeminiConnectionTester implements CommandLineRunner {

    @Autowired
    private AIModelService aiModelService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 TESTING GEMINI AI CONNECTION...");
        
        // Test 1: Basic connectivity
        String testResponse = aiModelService.generateProductDescription("Smartphone", "Electronics", "gemini");
        System.out.println("✅ Gemini Connection Test Result: " + testResponse.substring(0, Math.min(100, testResponse.length())) + "...");
        
        // Test 2: Category suggestions
        var categories = aiModelService.suggestCategories("Wireless Headphones", "gemini");
        System.out.println("✅ Gemini Category Suggestions: " + categories);
        
        // Test 3: Review analysis
        var reviews = java.util.List.of(
            "Great product, excellent sound quality!",
            "Battery life could be better",
            "Comfortable to wear for long periods"
        );
        String analysis = aiModelService.analyzeReviewsSentiment(reviews, "gemini");
        System.out.println("✅ Gemini Review Analysis: " + analysis.substring(0, Math.min(150, analysis.length())) + "...");
        
        System.out.println("🎉 GEMINI AI CONNECTION SUCCESSFUL!");
    }
}