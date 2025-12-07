package com.github.jimtrung.theater.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {
  private UUID id;
  private String username;
  private String email;
  private String password;
  private UserRole role;
  private String token;
  private Boolean verified;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public User() {}
  public User(
      UUID id, String username, String email, String password, UserRole role, String token, Boolean verified,
      OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.role = role;
    this.token = token;
    this.verified = verified;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public UserRole getRole() { return role; }
  public void setRole(UserRole role) { this.role = role; }

  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }

  public Boolean getVerified() { return verified; }
  public void setVerified(Boolean verified) { this.verified = verified; }

  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

  public OffsetDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
