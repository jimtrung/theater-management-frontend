package com.github.jimtrung.theater.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jimtrung.theater.dto.SeatStatusDTO;
import com.github.jimtrung.theater.dto.ShowtimeRevenueDTO;
import com.github.jimtrung.theater.model.Showtime;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ShowtimeService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final AuthTokenUtil authTokenUtil;

    public ShowtimeService(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public void insertShowtime(Showtime showtime) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String requestBody = mapper.writeValueAsString(showtime);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/showtimes"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 300) {
            throw new RuntimeException("Failed to insert showtime: (" + response.statusCode() + ") " + response.body());
        }
    }

    public List<Showtime> getAllShowtimes() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/showtimes"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        if (responseBody == null || responseBody.isBlank() || responseBody.startsWith("{")) {
            return Collections.emptyList();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());

        return mapper.readValue(responseBody, new TypeReference<List<Showtime>>() {});
    }

    public Showtime getShowtimeById(UUID id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/showtimes/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        if (body == null || body.isBlank() || body.startsWith("{\"timestamp")) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());

        return mapper.readValue(body, Showtime.class);
    }

    public void deleteShowtimeById(UUID id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/showtimes/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .DELETE()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void deleteAllShowtimes() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/showtimes"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .DELETE()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void updateShowtime(UUID id, Showtime showtime) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());
        String body = mapper.writeValueAsString(showtime);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/showtimes/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    public List<SeatStatusDTO> getSeatsWithStatus(UUID showtimeId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/showtimes/" + showtimeId + "/seats"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        if (body == null || body.isBlank() || body.startsWith("{")) {
            return Collections.emptyList();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());

        return mapper.readValue(body, new TypeReference<List<SeatStatusDTO>>() {});
    }
    public List<ShowtimeRevenueDTO> getShowtimeStats() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tickets/stats/showtime"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        if (body == null || body.isBlank() || body.startsWith("{")) {
            return Collections.emptyList();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(body, new TypeReference<List<ShowtimeRevenueDTO>>() {});
    }
}
