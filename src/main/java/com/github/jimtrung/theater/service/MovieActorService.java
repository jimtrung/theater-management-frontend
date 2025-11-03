package com.github.jimtrung.theater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jimtrung.theater.dto.MovieActorsRequest;
import com.github.jimtrung.theater.util.AuthTokenUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

public class MovieActorService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final AuthTokenUtil authTokenUtil;

    public MovieActorService(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public void insertMovieActors(UUID movieId, List<UUID> actorIds) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());

        MovieActorsRequest requestBodyObj = new MovieActorsRequest(movieId, actorIds);
        String requestBody = mapper.writeValueAsString(requestBodyObj);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/movie-actors/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        System.out.println("[DEBUG] - insertMovieActors - Sending POST request to /movie-actors...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] - insertMovieActors - Response status code: " + response.statusCode());
        System.out.println("[DEBUG] - insertMovieActors - Response body: " + response.body());
    }
}
