package com.project.orders.controller;

import com.project.orders.dto.request.OrderRequest;
import com.project.orders.dto.response.OrderResponse;
import com.project.orders.dto.response.PageResponse;
import com.project.orders.exception.ExceptionResponse;
import com.project.orders.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found by id", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public OrderResponse getOrderById(@PathVariable UUID id) {
        return orderService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved", content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public PageResponse<OrderResponse> getAllOrders(@RequestParam(required = false) UUID supplierId,
                                                    @RequestParam(required = false) UUID consumerId,
                                                    Pageable pageable) {
        return orderService.getAll(supplierId, consumerId, pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Order request not valid", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public OrderResponse createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        return orderService.create(orderRequest);
    }

}
