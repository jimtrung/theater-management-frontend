package com.github.jimtrung.theater.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Ticket {
  private UUID id;
  private UUID userId;
  private UUID showtimeId;
  private UUID seatId;
  private Integer price;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public Ticket(UUID id, UUID userId, UUID showtimeId, UUID seatId, Integer price, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.userId = userId;
    this.showtimeId = showtimeId;
    this.seatId = seatId;
    this.price = price;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UUID getShowtimeId() {
    return showtimeId;
  }

  public void setShowtimeId(UUID showtimeId) {
    this.showtimeId = showtimeId;
  }

  public UUID getSeatId() {
    return seatId;
  }

  public void setSeatId(UUID seatId) {
    this.seatId = seatId;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
