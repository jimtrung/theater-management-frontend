package com.github.jimtrung.theater.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class MovieActors {
    private UUID movieId;
    private UUID actorId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public MovieActors(UUID movieId, UUID actorId, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.movieId = movieId;
        this.actorId = actorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getMovieId() {
        return movieId;
    }

    public void setMovieId(UUID movieId) {
        this.movieId = movieId;
    }

    public UUID getActorId() {
        return actorId;
    }

    public void setActorId(UUID actorId) {
        this.actorId = actorId;
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
