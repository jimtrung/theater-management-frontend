package com.github.jimtrung.theater.dto;

import java.util.UUID;

public record MovieRevenueDTO(UUID movieId, String movieName, Long totalRevenue) {}
