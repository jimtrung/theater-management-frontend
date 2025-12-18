package com.github.jimtrung.theater.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jimtrung.theater.dto.PromotionRequest;
import com.github.jimtrung.theater.util.AuthTokenUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class PromotionService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final AuthTokenUtil authTokenUtil;
    private final ObjectMapper objectMapper;

    public PromotionService(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
        // Khởi tạo ObjectMapper dùng chung để tối ưu hiệu suất
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Lấy toàn bộ danh sách khuyến mãi từ Backend
     */
    public List<PromotionRequest> getAllPromotions() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/promotions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .GET()
                .build();

        System.out.println("[DEBUG] - getAllPromotions - Sending GET request to /promotions...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] - getAllPromotions - Response status code: " + response.statusCode());

        String responseBody = response.body();

        if (responseBody == null || responseBody.isBlank() || response.statusCode() == 204) {
            System.out.println("[DEBUG] - getAllPromotions - No promotions found");
            return Collections.emptyList();
        }

        List<PromotionRequest> promotions = objectMapper.readValue(responseBody, new TypeReference<List<PromotionRequest>>() {});
        System.out.println("[DEBUG] - getAllPromotions - Parsed promotions count: " + promotions.size());
        return promotions;
    }

    /**
     * Lấy danh sách các khuyến mãi đang còn hiệu lực
     */
    public List<PromotionRequest> getActivePromotions() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/promotions/active"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .GET()
                .build();

        System.out.println("[DEBUG] - getActivePromotions - Sending GET request to /promotions/active...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] - getActivePromotions - Response status code: " + response.statusCode());

        String responseBody = response.body();

        if (responseBody == null || responseBody.isBlank() || response.statusCode() == 204) {
            return Collections.emptyList();
        }

        return objectMapper.readValue(responseBody, new TypeReference<List<PromotionRequest>>() {});
    }
}
