package com.github.jimtrung.theater.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.model.Movie;
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

    public void insertAuditorium(Auditorium auditorium) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());

        String requestBody = mapper.writeValueAsString(auditorium);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auditoriums"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        System.out.println("[DEBUG] - insertMovie - Sending POST request to /movies...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] - insertMovie - Response status code: " + response.statusCode());
        System.out.println("[DEBUG] - insertMovie - Response body: " + response.body());
    }

    public List<Auditorium> getAllAuditoriums() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auditoriums"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .GET()
                .build();

        System.out.println("[DEBUG] - getAllMovies - Sending GET request to /movies...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] - getAllMovies - Response status code: " + response.statusCode());
        System.out.println("[DEBUG] - getAllMovies - Response body: " + response.body());

        String responseBody = response.body();

        if (responseBody == null || responseBody.isBlank()) {
            System.out.println("[DEBUG] - getAllMovies - Movies in DB is empty");
            return Collections.emptyList();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.registerModule(new JavaTimeModule());

        List<Auditorium> auditoriums = objectMapper.readValue(responseBody, new TypeReference<List<Auditorium>>() {});
        System.out.println("[DEBUG] - getALlMovies - Parsed movies count: " + auditoriums.size());
        return auditoriums;
    }

    public Auditorium getAuditoriumById(UUID id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auditoriums/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .GET()
                .build();

        System.out.println("[DEBUG] - getMovieById - Sending GET request to /movies...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] - getMovieById - Response status code: " + response.statusCode());
        System.out.println("[DEBUG] - getMovieById - Response body: " + response.body());

        String responseBody = response.body();

        if (responseBody == null || responseBody.isBlank()) {
            System.out.println("[DEBUG] - getMovieById - Movies in DB is empty");
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.registerModule(new JavaTimeModule());

        Auditorium auditorium = objectMapper.readValue(responseBody, new TypeReference<Auditorium>() {});
        System.out.println("[DEBUG] - getMovieById - Movie name: " + auditorium.getName());
        return auditorium;
    }

    public void deleteAuditoriumById(UUID id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auditoriums/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .DELETE()
                .build();

        System.out.println("[DEBUG] - deleteMovieById - Sending GET request to /movies...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] - deleteMovieById - Response status code: " + response.statusCode());
        System.out.println("[DEBUG] - deleteMovieById - Response body: " + response.body());
    }

    public void deleteAllAuditoriums() throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auditoriums"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        System.out.println("[DEBUG] - deleteAllMovies - Sending POST request to /movies...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] - deleteAllMovies - Response status code: " + response.statusCode());
        System.out.println("[DEBUG] - deleteAllMovies - Response body: " + response.body());
    }

    public void updateAuditorium(UUID id, Auditorium auditorium) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());

        String requestBody = mapper.writeValueAsString(auditorium);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auditoriums/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        System.out.println("[DEBUG] - updateMovie - Sending PUT request to /movies/" + id);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] - updateMovie - Response status code: " + response.statusCode());
        System.out.println("[DEBUG] - updateMovie - Response body: " + response.body());
    }
}
