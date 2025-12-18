package com.github.jimtrung.theater.model;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model đại diện cho Khuyến mãi ở phía Frontend.
 * Sử dụng record để đồng bộ với phong cách của BillRequest.
 */
public record Promotion(
        String name,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        String description
) {
    // Định dạng ngày tháng để hiển thị lên giao diện (Ví dụ: 18/12/2025)
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Trả về chuỗi ngày bắt đầu đã định dạng
     */
    public String getFormattedStartDate() {
        return startDate != null ? startDate.format(formatter) : "";
    }

    /**
     * Trả về chuỗi ngày kết thúc đã định dạng
     */
    public String getFormattedEndDate() {
        return endDate != null ? endDate.format(formatter) : "";
    }

    /**
     * Kiểm tra xem khuyến mãi có đang trong thời gian hiệu lực hay không
     */
    public boolean isActive() {
        OffsetDateTime now = OffsetDateTime.now();
        return startDate != null && endDate != null &&
                now.isAfter(startDate) && now.isBefore(endDate);
    }
}