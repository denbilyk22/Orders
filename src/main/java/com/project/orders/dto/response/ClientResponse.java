package com.project.orders.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record ClientResponse(UUID id,
                             String name,
                             String email,
                             String address,
                             Boolean active,
                             ZonedDateTime createdDate,
                             BigDecimal profit) {
}
