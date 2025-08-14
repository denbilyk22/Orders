package com.project.orders.mapper;

import com.project.orders.dto.request.OrderRequest;
import com.project.orders.dto.response.OrderResponse;
import com.project.orders.dto.response.PageResponse;
import com.project.orders.persistence.model.Order;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", uses = ClientMapper.class)
public interface OrderMapper {

    Order toEntity(OrderRequest orderRequest);

    OrderResponse toDto(Order order);

    List<OrderResponse> toDtos(List<Order> orders);

    default PageResponse<OrderResponse> toPageResponse(Page<Order> page) {
        return PageResponse.<OrderResponse>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(toDtos(page.getContent()))
                .build();
    }
}
