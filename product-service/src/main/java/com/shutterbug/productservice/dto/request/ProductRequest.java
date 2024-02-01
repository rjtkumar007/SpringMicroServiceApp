package com.shutterbug.productservice.dto.request;

import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
public record ProductRequest(@NonNull String name, @NonNull String description,@NonNull BigDecimal price) {
}
