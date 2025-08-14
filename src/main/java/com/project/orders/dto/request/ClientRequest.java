package com.project.orders.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRequest(@NotBlank @Size(min = 3, max = 255) String name,
                            @NotBlank @Email String email,
                            String address) {
}
