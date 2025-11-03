package com.github.jimtrung.theater.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jimtrung.theater.model.Actor;
import com.github.jimtrung.theater.dto.ErrorResponse;
import com.github.jimtrung.theater.util.AuthTokenUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class ActorService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final AuthTokenUtil authTokenUtil;
    private final ObjectMapper mapper;

    public ActorService(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
        this.mapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .registerModule(new JavaTimeModule());
    }

    public Object getAllActors() throws Exception {
        System.out.println("[DEBUG] Loading actors...");
        String token = authTokenUtil.loadAccessToken();
        System.out.println("[DEBUG] Using token: " + token);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/actor/all"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[DEBUG] - getAllActors - Response status code: " + response.statusCode());
        System.out.println("[DEBUG] - getAllActors - Response body: " + response.body());

        if (response.statusCode() != 200) {
            System.out.println("[DEBUG] - getAllActors - Failed to fetch actors.");
            return mapper.readValue(response.body(), ErrorResponse.class);
        }

        if (response.body() == null || response.body().isBlank()) {
            System.out.println("[DEBUG] - getAllActors - Empty list received.");
            return Collections.emptyList();
        }

        List<Actor> actors = mapper.readValue(response.body(), new TypeReference<List<Actor>>() {});
        System.out.println("[DEBUG] Loaded " + actors.size() + " actors.");
        return actors;
    }
}
