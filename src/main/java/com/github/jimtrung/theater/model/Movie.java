package com.github.jimtrung.theater.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Movie {
  private UUID id;
  private UUID directorId;
  private String name;
  private String description;
  private MovieGenre[] genres;
  private OffsetDateTime premiere;
  private Integer duration;
  private String language;
  private Integer rated;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public Movie(UUID id, UUID directorId, String name, String description, MovieGenre[] movieGenres,
    OffsetDateTime premiere, Integer duration, String language, Integer rated, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.directorId = directorId;
    this.name = name;
    this.description = description;
    this.genres = movieGenres;
    this.premiere = premiere;
    this.duration = duration;
    this.language = language;
    this.rated = rated;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getDirectorId() {
    return directorId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public MovieGenre[] getGenres() {
    return genres;
  }

  public OffsetDateTime getPremiere() {
    return premiere;
  }

  public Integer getDuration() {
    return duration;
  }

  public String getLanguage() {
    return language;
  }

  public Integer getRated() {
    return rated;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setDirectorId(UUID directorId) {
    this.directorId = directorId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setGenres(MovieGenre[] genres) {
    this.genres = genres;
  }

  public void setPremiere(OffsetDateTime premiere) {
    this.premiere = premiere;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public void setRated(Integer rated) {
    this.rated = rated;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
