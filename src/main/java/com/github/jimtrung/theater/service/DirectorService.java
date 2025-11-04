package com.github.jimtrung.theater.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jimtrung.theater.model.Director;
import com.github.jimtrung.theater.dto.ErrorResponse;
import com.github.jimtrung.theater.util.AuthTokenUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class DirectorService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final AuthTokenUtil authTokenUtil;
    private final ObjectMapper mapper;

    public DirectorService(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
        this.mapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .registerModule(new JavaTimeModule());
    }

    public Object getAllDirectors() throws Exception {
        System.out.println("[DEBUG] Loading directors...");
        String token = authTokenUtil.loadAccessToken();
        System.out.println("[DEBUG] Using token: " + token);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/director/all"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[DEBUG] - getAllDirectors - Response status code: " + response.statusCode());
        System.out.println("[DEBUG] - getAllDirectors - Response body: " + response.body());

        if (response.statusCode() != 200) {
            System.out.println("[DEBUG] - getAllDirectors - Failed to fetch directors.");
            return mapper.readValue(response.body(), ErrorResponse.class);
        }

        if (response.body() == null || response.body().isBlank()) {
            System.out.println("[DEBUG] - getAllDirectors - Empty list received.");
            return Collections.emptyList();
        }

        List<Director> directors = mapper.readValue(response.body(), new TypeReference<List<Director>>() {});
        System.out.println("[DEBUG] Loaded " + directors.size() + " directors.");
        return directors;
    }
}
