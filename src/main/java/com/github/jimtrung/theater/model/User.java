package com.github.jimtrung.theater.model;

import javafx.fxml.FXML;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {
  /* Attributes */
  private UUID id;
  private String username;
  private String email;
  private String phoneNumber;
  private String password;
  private UserRole role;
  private Provider provider;
  private String token;
  private Integer otp;
  private Boolean verified;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  /* Constructors */
  public User() {
  }

  public User(
      UUID id, String username, String email, String phoneNumber, String password, UserRole role, Provider provider,
      String token, Integer otp, Boolean verified, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.password = password;
    this.role = role;
    this.provider = provider;
    this.token = token;
    this.otp = otp;
    this.verified = verified;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /* Functions */
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public Provider getProvider() {
    return provider;
  }

  public void setProvider(Provider provider) {
    this.provider = provider;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Integer getOtp() {
    return otp;
  }

  public void setOtp(Integer otp) {
    this.otp = otp;
  }

  public Boolean getVerified() {
    return verified;
  }

  public void setVerified(Boolean verified) {
    this.verified = verified;
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
