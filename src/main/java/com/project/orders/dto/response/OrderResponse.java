package com.project.orders.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record OrderResponse(UUID id,
                            String name,
                            BigDecimal price,
                            ZonedDateTime startProcessingTime,
                            ZonedDateTime endProcessingTime,
                            ZonedDateTime createdDate,
                            ClientResponse supplier,
                            ClientResponse consumer) {
}
