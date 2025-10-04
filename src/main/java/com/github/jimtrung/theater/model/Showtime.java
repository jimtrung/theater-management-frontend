package com.github.jimtrung.theater.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Showtime {
  private UUID id;
  private UUID movieId;
  private UUID auditoriumId;
  private OffsetDateTime startTime;
  private OffsetDateTime endTime;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public Showtime(UUID id, UUID movieId, UUID auditoriumId, OffsetDateTime startTime, OffsetDateTime endTime, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.movieId = movieId;
    this.auditoriumId = auditoriumId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getMovieId() {
    return movieId;
  }

  public void setMovieId(UUID movieId) {
    this.movieId = movieId;
  }

  public UUID getAuditoriumId() {
    return auditoriumId;
  }

  public void setAuditoriumId(UUID auditoriumId) {
    this.auditoriumId = auditoriumId;
  }

  public OffsetDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(OffsetDateTime startTime) {
    this.startTime = startTime;
  }

  public OffsetDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(OffsetDateTime endTime) {
    this.endTime = endTime;
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
