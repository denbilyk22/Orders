package com.project.orders.exception;

import lombok.Builder;

@Builder
public record ExceptionResponse(int status, String message) {
}
