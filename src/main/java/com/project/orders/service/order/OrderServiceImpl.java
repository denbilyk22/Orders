package com.project.orders.service.order;

import com.project.orders.dto.request.OrderRequest;
import com.project.orders.dto.response.OrderResponse;
import com.project.orders.dto.response.PageResponse;
import com.project.orders.enums.ClientBalanceChangeType;
import com.project.orders.exception.ApiException;
import com.project.orders.mapper.OrderMapper;
import com.project.orders.persistence.model.Client;
import com.project.orders.persistence.model.ClientBalanceChange;
import com.project.orders.persistence.model.Order;
import com.project.orders.persistence.repository.ClientBalanceChangeRepository;
import com.project.orders.persistence.repository.ClientRepository;
import com.project.orders.persistence.repository.OrderRepository;
import com.project.orders.persistence.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.project.orders.enums.ClientBalanceChangeType.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ClientBalanceChangeRepository clientBalanceChangeRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getById(UUID id) {
        var order = findOrderById(id);
        return orderMapper.toDto(order);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<OrderResponse> getAll(UUID supplierId, UUID consumerId, Pageable pageable) {
        var specification = OrderSpecification.get(supplierId, consumerId);
        var page = orderRepository.findAll(specification, pageable);
        return orderMapper.toPageResponse(page);
    }

    @Transactional(timeout = 15)
    @Override
    public OrderResponse create(OrderRequest orderRequest) {
        validateOrderRequest(orderRequest);
        validateOrderUniqueness(orderRequest.name(), orderRequest.supplierId(), orderRequest.consumerId());

        var supplier = findClientById(orderRequest.supplierId());
        var consumer = findClientById(orderRequest.consumerId());

        validateClientActiveStatus(supplier);
        validateClientActiveStatus(consumer);
        validateConsumerBalanceDecreasing(consumer, orderRequest.price());

        addOrderProcessingDelay();

        var order = orderMapper.toEntity(orderRequest);
        order.setSupplier(supplier);
        order.setConsumer(consumer);

        orderRepository.save(order);

        var supplierBalanceChange = createBalanceChange(order.getPrice(), ORDER_CREATION, supplier, order);
        var consumerBalanceChange = createBalanceChange(order.getPrice().negate(), ORDER_CREATION, consumer, order);
        clientBalanceChangeRepository.saveAll(List.of(supplierBalanceChange, consumerBalanceChange));

        return orderMapper.toDto(order);
    }

    private Order findOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "Order not found by id"));
    }

    private Client findClientById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "Client not found by id"));
    }

    private void validatePrice(BigDecimal price) {
        if (BigDecimal.ZERO.compareTo(price) > -1) {
            throw new ApiException(400, "Order price must be greater than 0");
        }
    }

    private void validateProcessingTimes(ZonedDateTime startProcessingTime, ZonedDateTime endProcessingTime) {
        if (startProcessingTime.compareTo(endProcessingTime) > 0) {
            throw new ApiException(400, "Start processing time must be less than or equal to end processing time");
        }
    }

    private void validateSupplierAndConsumer(UUID supplierId, UUID consumerId) {
        if (supplierId.equals(consumerId)) {
            throw new ApiException(400, "Order supplier and consumer must be not the same client");
        }
    }

    private void validateOrderRequest(OrderRequest orderRequest) {
        validatePrice(orderRequest.price());
        validateProcessingTimes(orderRequest.startProcessingTime(), orderRequest.endProcessingTime());
        validateSupplierAndConsumer(orderRequest.supplierId(), orderRequest.consumerId());
    }

    private void validateOrderUniqueness(String name, UUID supplierId, UUID consumerId) {
        if (orderRepository.isSimilarOrderExist(name, supplierId, consumerId)) {
            throw new ApiException(409, "Similar order already exists");
        }
    }

    private void validateConsumerBalanceDecreasing(Client consumer, BigDecimal price) {
        var clientProfit = clientBalanceChangeRepository.getClientProfit(consumer.getId());
        var finalProfit = clientProfit.subtract(price);

        if (BigDecimal.valueOf(-1000).compareTo(finalProfit) > 0) {
            throw new ApiException(409, "Consumer balance cannot be decreased to more than 1000");
        }
    }

    private void validateClientActiveStatus(Client client) {
        if (Boolean.FALSE.equals(client.getActive())) {
            throw new ApiException(409, "Client '%s' is not active".formatted(client.getName()));
        }
    }

    private ClientBalanceChange createBalanceChange(BigDecimal amount, ClientBalanceChangeType changeType, Client client,
                                                    Order order) {
        return ClientBalanceChange.builder()
                .amount(amount)
                .changeType(changeType)
                .client(client)
                .order(order)
                .build();
    }

    private void addOrderProcessingDelay() {
        var random = new Random(System.currentTimeMillis());
        var timeoutTimeSeconds = random.nextLong(10) + 1;
        try {
            Thread.sleep(timeoutTimeSeconds * 1000);
        } catch (InterruptedException e) {
            throw new ApiException(500, "Order processing failed", e);
        }
    }

}
