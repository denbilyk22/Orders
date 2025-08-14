package com.project.orders.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record OrderRequest(@NotBlank @Size(max = 255) String name,
                           @NotNull BigDecimal price,
                           @NotNull ZonedDateTime startProcessingTime,
                           @NotNull ZonedDateTime endProcessingTime,
                           @NotNull UUID supplierId,
                           @NotNull UUID consumerId) {
}
