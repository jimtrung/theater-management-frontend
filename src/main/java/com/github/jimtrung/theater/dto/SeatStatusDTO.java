package com.github.jimtrung.theater.dto;

import com.github.jimtrung.theater.model.Seat;

public record SeatStatusDTO(Seat seat, boolean isBooked) {}
