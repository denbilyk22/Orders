package com.project.orders.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderRequest(@NotBlank @Size(max = 255) String name,
                           @NotNull BigDecimal price,
                           @NotNull UUID supplierId,
                           @NotNull UUID consumerId) {
}
