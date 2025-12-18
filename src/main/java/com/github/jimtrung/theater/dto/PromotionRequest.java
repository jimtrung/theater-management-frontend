package com.github.jimtrung.theater.dto;

import java.time.OffsetDateTime;

/**
 * DTO đại diện cho thông tin khuyến mãi hiển thị trên giao diện.
 * Thứ tự các trường này phải khớp hoàn toàn với Constructor trong PromotionService ở Backend.
 */
public record PromotionRequest(
        String name,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        String description
) {
    // Bạn có thể thêm helper method để định dạng ngày tháng hiển thị trên UI
    public String getFormattedRange() {
        return String.format("%s - %s", startDate.toLocalDate(), endDate.toLocalDate());
    }
}