package com.github.jimtrung.theater.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Auditorium {
    private UUID id;
    private String name;
    private String type;
    private Integer capacity;
    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Auditorium() {};
    public Auditorium(UUID id, String name, String type, Integer capacity, String note) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.note = note;
    }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
