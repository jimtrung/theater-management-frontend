package com.github.jimtrung.theater.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.util.AuthTokenUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AuditoriumService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final AuthTokenUtil authTokenUtil;

    public AuditoriumService(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    // === CREATE ===
    public void insertAuditorium(Auditorium auditorium) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());

        String requestBody = mapper.writeValueAsString(auditorium);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auditoriums"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        System.out.println("[DEBUG] - insertAuditorium - Sending POST request to /auditoriums...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[DEBUG] - insertAuditorium - Response status: " + response.statusCode());
        System.out.println("[DEBUG] - insertAuditorium - Response body: " + response.body());
    }

    // === READ ALL ===
    public List<Auditorium> getAllAuditoriums() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auditoriums"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .GET()
                .build();

        System.out.println("[DEBUG] - getAllAuditoriums - Sending GET request to /auditoriums...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[DEBUG] - getAllAuditoriums - Status: " + response.statusCode());
        System.out.println("[DEBUG] - getAllAuditoriums - Body: " + response.body());

        // If backend returns error object instead of array -> avoid parsing error
        String responseBody = response.body();
        if (responseBody == null || responseBody.isBlank() || responseBody.startsWith("{")) {
            System.out.println("[DEBUG] - getAllAuditoriums - Empty or invalid response (not a list).");
            return Collections.emptyList();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());

        List<Auditorium> auditoriums = mapper.readValue(responseBody, new TypeReference<List<Auditorium>>() {});
        System.out.println("[DEBUG] - getAllAuditoriums - Parsed auditoriums count: " + auditoriums.size());
        return auditoriums;
    }

    // === READ ONE ===
    public Auditorium getAuditoriumById(UUID id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auditoriums/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .GET()
                .build();

        System.out.println("[DEBUG] - getAuditoriumById - Sending GET request to /auditoriums/" + id);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[DEBUG] - getAuditoriumById - Status: " + response.statusCode());
        System.out.println("[DEBUG] - getAuditoriumById - Body: " + response.body());

        String body = response.body();
        if (body == null || body.isBlank() || body.startsWith("{\"timestamp")) {
            System.out.println("[DEBUG] - getAuditoriumById - Empty or error response");
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());

        return mapper.readValue(body, Auditorium.class);
    }

    // === DELETE ONE ===
    public void deleteAuditoriumById(UUID id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auditoriums/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .DELETE()
                .build();

        System.out.println("[DEBUG] - deleteAuditoriumById - Sending DELETE request to /auditoriums/" + id);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[DEBUG] - deleteAuditoriumById - Status: " + response.statusCode());
        System.out.println("[DEBUG] - deleteAuditoriumById - Body: " + response.body());
    }

    // === DELETE ALL ===
    public void deleteAllAuditoriums() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auditoriums"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        System.out.println("[DEBUG] - deleteAllAuditoriums - Sending DELETE request to /auditoriums...");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[DEBUG] - deleteAllAuditoriums - Status: " + response.statusCode());
        System.out.println("[DEBUG] - deleteAllAuditoriums - Body: " + response.body());
    }

    // === UPDATE ===
    public void updateAuditorium(UUID id, Auditorium auditorium) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());

        String body = mapper.writeValueAsString(auditorium);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auditoriums/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        System.out.println("[DEBUG] - updateAuditorium - Sending PUT request to /auditoriums/" + id);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[DEBUG] - updateAuditorium - Status: " + response.statusCode());
        System.out.println("[DEBUG] - updateAuditorium - Body: " + response.body());
    }
}
