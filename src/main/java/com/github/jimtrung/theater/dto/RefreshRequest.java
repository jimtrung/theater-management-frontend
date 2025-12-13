package com.github.jimtrung.theater.dto;

import java.util.UUID;

public record RefreshRequest(String refreshToken) {
    public static record MovieRevenueDTO(UUID movieId, String movieName, Long totalRevenue) {}
}
