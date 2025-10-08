package com.github.jimtrung.theater.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.util.AuthTokenUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class MovieService {
    private final HttpClient client = HttpClient.newHttpClient();

    private final AuthTokenUtil authTokenUtil;
    public MovieService(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public void insertMovie(Movie movie) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());

        String requestBody = mapper.writeValueAsString(movie);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/movies"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        System.out.println("[DEBUG] - insertMovie - Sending POST request to /movies...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] - insertMovie - Response status code: " + response.statusCode());
        System.out.println("[DEBUG] - insertMovie - Response body: " + response.body());
    }

    public void deleteAllMovies() throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/movies"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        System.out.println("[DEBUG] - deleteAllMovies - Sending POST request to /movies...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] - deleteAllMovies - Response status code: " + response.statusCode());
        System.out.println("[DEBUG] - deleteAllMovies - Response body: " + response.body());
    }

    public List<Movie> getAllMovies() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/movies"))
                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
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

        List<Movie> movies = objectMapper.readValue(responseBody, new TypeReference<List<Movie>>() {});
        System.out.println("[DEBUG] - getALlMovies - Parsed movies count: " + movies.size());
        return movies;
    }
}
