package com.github.jimtrung.theater.model;

import java.time.OffsetDateTime;

public class Country {
  private String code;
  private String name;
  private String iso3;
  private String phoneCode;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public Country(String code, String name, String iso3, String phoneCode, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.code = code;
    this.name = name;
    this.iso3 = iso3;
    this.phoneCode = phoneCode;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getCode() {
    return code;
  }
  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIso3() {
    return iso3;
  }

  public void setIso3(String iso3) {
    this.iso3 = iso3;
  }

  public String getPhoneCode() {
    return phoneCode;
  }

  public void setPhoneCode(String phoneCode) {
    this.phoneCode = phoneCode;
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

