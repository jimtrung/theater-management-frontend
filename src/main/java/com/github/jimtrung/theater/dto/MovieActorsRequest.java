package com.github.jimtrung.theater.dto;

import java.util.List;
import java.util.UUID;

public record MovieActorsRequest(UUID movieId, List<UUID> actorsId) {}
