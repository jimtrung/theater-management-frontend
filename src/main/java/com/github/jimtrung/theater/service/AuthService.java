package com.github.jimtrung.theater.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jimtrung.theater.dto.ErrorResponse;
import com.github.jimtrung.theater.dto.TokenPair;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import com.sun.net.httpserver.Request;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final AuthTokenUtil authTokenUtil;

    public AuthService(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public Object signUp(User user) throws Exception {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("username", user.getUsername());
        bodyMap.put("email", user.getEmail());
        bodyMap.put("phone_number", user.getPhoneNumber());
        bodyMap.put("password", user.getPassword());

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(bodyMap);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/auth/signup"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        if (response.statusCode() != 201) {
            return mapper.readValue(response.body(), ErrorResponse.class);
        }

        return response.body();
    }

    public Object signIn(User user) throws Exception {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("username", user.getUsername());
        bodyMap.put("password", user.getPassword());

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(bodyMap);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/auth/signin"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        if (response.statusCode() != 200) {
            return mapper.readValue(response.body(), ErrorResponse.class);
        }

        return mapper.readValue(response.body(), TokenPair.class);
    }

    public User getUser() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/user/"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(response.body(), User.class);
    }

    public String refresh() throws Exception {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("refresh_token", authTokenUtil.loadRefreshToken());

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(bodyMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auth/refresh"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
