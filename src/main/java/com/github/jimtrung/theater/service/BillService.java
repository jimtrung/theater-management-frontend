package com.github.jimtrung.theater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jimtrung.theater.dto.BillRequest;
import com.github.jimtrung.theater.dto.ErrorResponse;
import com.github.jimtrung.theater.util.AuthTokenUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class BillService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final AuthTokenUtil authTokenUtil;
    private final ObjectMapper mapper;
    private final String BASE_URL = "http://localhost:8080/bills";

    public BillService(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
        this.mapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .registerModule(new JavaTimeModule());
    }

    /**
     * Hàm gọi API tạo hóa đơn tương ứng với BillController phía Backend
     */
    public Object createBill(UUID userId, BillRequest billRequest) throws Exception {
        // Chuyển đối tượng BillRequest sang chuỗi JSON
        String requestBody = mapper.writeValueAsString(billRequest);

        // Xây dựng request POST tới /bills/{userId}
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + userId.toString()))
                .header("Content-Type", "application/json")
                // Gửi kèm Access Token nếu API yêu cầu bảo mật
                .header("Authorization", "Bearer " + authTokenUtil.loadAccessToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Gửi request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Kiểm tra nếu không thành công (không phải 200 OK)
        if (response.statusCode() != 200) {
            try {
                return mapper.readValue(response.body(), ErrorResponse.class);
            } catch (Exception e) {
                return "Lỗi hệ thống: " + response.statusCode();
            }
        }

        // Trả về thông báo thành công từ Backend
        return response.body();
    }
}