package com.project.orders.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResponse<T>(int page, int size, int totalPages, long totalElements, List<T> content) {
}
