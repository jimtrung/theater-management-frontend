package com.github.jimtrung.theater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jimtrung.theater.dto.BookingRequest;
import com.github.jimtrung.theater.model.Ticket;
import com.github.jimtrung.theater.util.AuthTokenUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

public class TicketService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final AuthTokenUtil authTokenUtil;
    private final ObjectMapper mapper;

    public TicketService(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
        this.mapper = new ObjectMapper();
        this.mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.mapper.registerModule(new JavaTimeModule());
    }

    public List<Ticket> bookTickets(BookingRequest request) throws Exception {
        String requestBody = mapper.writeValueAsString(request);
        
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tickets/book"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
                
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to book tickets: " + response.body());
        }
        
        return mapper.readValue(response.body(), mapper.getTypeFactory().constructCollectionType(List.class, Ticket.class));
    }

    public List<Ticket> getUserTickets() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tickets/user"))
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to load tickets: " + response.body());
        }

        return mapper.readValue(response.body(), mapper.getTypeFactory().constructCollectionType(List.class, Ticket.class));
    }

    public void payTickets(List<UUID> ticketIds) throws Exception {
        String requestBody = mapper.writeValueAsString(ticketIds);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tickets/pay"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Payment failed: " + response.body());
        }
    }
}
