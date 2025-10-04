package com.github.jimtrung.theater.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Seat {
  private UUID id;
  private UUID auditoriumId;
  private String row;
  private Integer number;
  private OffsetDateTime updatedAt;
  private OffsetDateTime createdAt;

  public Seat(UUID id, UUID auditoriumId, String row, Integer number, OffsetDateTime updatedAt, OffsetDateTime createdAt) {
    this.id = id;
    this.auditoriumId = auditoriumId;
    this.row = row;
    this.number = number;
    this.updatedAt = updatedAt;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getAuditoriumId() {
    return auditoriumId;
  }

  public void setAuditoriumId(UUID auditoriumId) {
    this.auditoriumId = auditoriumId;
  }

  public String getRow() {
    return row;
  }

  public void setRow(String row) {
    this.row = row;
  }

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
