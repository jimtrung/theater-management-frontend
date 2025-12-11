package com.github.jimtrung.theater.dto;

import java.util.UUID;

public record ShowtimeRevenueDTO(
    UUID showtimeId,
    Long soldTickets,
    Long revenue
) {}
