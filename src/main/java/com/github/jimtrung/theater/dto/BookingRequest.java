package com.github.jimtrung.theater.dto;

import java.util.List;
import java.util.UUID;

public record BookingRequest(UUID showtimeId, List<UUID> seatIds) {}
