package com.github.jimtrung.theater.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Movie {
    private UUID id;
    private String name;
    private String author;
    private String genres;
    private int ageLimit;
    private String description;
    private Integer duration;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Movie() {};
    public Movie(UUID id, String name, String author, String genres, int ageLimit, String description, Integer duration) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.genres = genres;
        this.ageLimit = ageLimit;
        this.description = description;
        this.duration = duration;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getGenres() {
        return genres;
    }

    public Integer getDuration() {
        return duration;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(int ageLimit) {
        this.ageLimit = ageLimit;
    }
}
