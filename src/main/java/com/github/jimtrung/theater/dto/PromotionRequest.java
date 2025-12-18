package com.github.jimtrung.theater.dto;

import java.time.OffsetDateTime;

public record PromotionRequest(
        String name,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        String description,
        String imageUrl
) {
}
