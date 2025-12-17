package com.github.jimtrung.theater.dto;

public record BillRequest(String to, String movieTitle, String movieRating, String showDate, String showTime,
                          String cinemaName, String seats, String screenRoom, String concessions,
                          String ticketCode, String totalPrice, String paymentMethod) {

}
