package com.github.jimtrung.theater.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Movie {
    private UUID id;
    private String name;
    private String description;
    private String director;
    private List<String> actors;
    private List<String> genres;
    private OffsetDateTime premiere;
    private Integer duration;
    private MovieLanguage language; 
    private Integer rated;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    
    public Movie() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public List<String> getActors() { return actors; }
    public void setActors(List<String> actors) { this.actors = actors; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public OffsetDateTime getPremiere() { return premiere; }
    public void setPremiere(OffsetDateTime premiere) { this.premiere = premiere; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public MovieLanguage getLanguage() { return language; }
    public void setLanguage(MovieLanguage language) { this.language = language; }

    public Integer getRated() { return rated; }
    public void setRated(Integer rated) { this.rated = rated; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
