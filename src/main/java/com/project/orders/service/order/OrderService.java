package com.project.orders.service.order;

import com.project.orders.dto.request.OrderRequest;
import com.project.orders.dto.response.OrderResponse;
import com.project.orders.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    OrderResponse getById(UUID id);
    PageResponse<OrderResponse> getAll(UUID supplierId, UUID consumerId, Pageable pageable);
    OrderResponse create(OrderRequest orderRequest);
}
