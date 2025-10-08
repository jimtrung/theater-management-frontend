package com.github.jimtrung.theater.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TokenPair(String refreshToken, String accessToken) {}
