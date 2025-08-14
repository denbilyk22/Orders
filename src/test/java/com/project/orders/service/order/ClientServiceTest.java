package com.project.orders.service.order;

import com.project.orders.exception.ApiException;
import com.project.orders.mapper.OrderMapper;
import com.project.orders.persistence.model.Order;
import com.project.orders.persistence.repository.ClientBalanceChangeRepository;
import com.project.orders.persistence.repository.ClientRepository;
import com.project.orders.persistence.repository.OrderRepository;
import com.project.orders.persistence.specification.OrderSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientBalanceChangeRepository clientBalanceChangeRepository;

    @Spy
    private OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Nested
    class GetByIdTest {

        private final UUID id = UUID.randomUUID();

        @Test
        void getById() {
            // given
            var order = Order.builder()
                    .id(id)
                    .name("Order")
                    .price(BigDecimal.TEN)
                    .createdDate(ZonedDateTime.now())
                    .build();

            Mockito.when(orderRepository.findById(id))
                    .thenReturn(Optional.of(order));

            // when
            var result = orderService.getById(id);

            // then
            Assertions.assertEquals(id, result.id());
            Assertions.assertEquals("Order", result.name());
            Assertions.assertEquals(BigDecimal.TEN, result.price());
            Assertions.assertNull(result.startProcessingTime());
            Assertions.assertNull(result.endProcessingTime());
            Assertions.assertNull(result.supplier());
            Assertions.assertNull(result.consumer());
            Assertions.assertNotNull(result.createdDate());
        }

        @Test
        void getNotFoundException() {
            // given
            Mockito.when(orderRepository.findById(id))
                    .thenReturn(Optional.empty());

            // when && then
            Assertions.assertThrows(ApiException.class, () -> orderService.getById(id));
        }
    }

    @Nested
    class GetAllTest {

        @Test
        void getAll() {
            // given
            var id = UUID.randomUUID();

            var order = Order.builder()
                    .id(id)
                    .name("Order")
                    .price(BigDecimal.TEN)
                    .createdDate(ZonedDateTime.now())
                    .build();

            var specification = OrderSpecification.get(null, null);

            var pageRequest = PageRequest.of(0, 10);

            var orderList = List.of(order);

            var page = new PageImpl<>(orderList, pageRequest, 1);

            Mockito.when(orderRepository.findAll(specification, pageRequest))
                    .thenReturn(page);

            // when
            var result = orderService.getAll(null, null, pageRequest);

            // then
            Assertions.assertEquals(0, result.page());
            Assertions.assertEquals(10, result.size());
            Assertions.assertEquals(1, result.totalPages());
            Assertions.assertEquals(1, result.totalElements());
            Assertions.assertEquals(1, result.content().size());

            var resultOrder = result.content().get(0);
            Assertions.assertEquals(id, resultOrder.id());
            Assertions.assertEquals("Order", resultOrder.name());
            Assertions.assertEquals(BigDecimal.TEN, resultOrder.price());
            Assertions.assertNull(resultOrder.startProcessingTime());
            Assertions.assertNull(resultOrder.endProcessingTime());
            Assertions.assertNull(resultOrder.supplier());
            Assertions.assertNull(resultOrder.consumer());
            Assertions.assertNotNull(resultOrder.createdDate());
        }

        @Test
        void getEmptyResult() {
            // given
            var specification = OrderSpecification.get(null, null);

            var pageRequest = PageRequest.of(0, 10);

            var orderList = List.<Order>of();

            var page = new PageImpl<>(orderList, pageRequest, 0);

            Mockito.when(orderRepository.findAll(specification, pageRequest))
                    .thenReturn(page);

            // when
            var result = orderService.getAll(null, null, pageRequest);

            // then
            Assertions.assertEquals(0, result.page());
            Assertions.assertEquals(10, result.size());
            Assertions.assertEquals(1, result.totalPages());
            Assertions.assertEquals(0, result.totalElements());
            Assertions.assertEquals(0, result.content().size());
        }

    }

    @Nested
    class CreateTest {
        @Test
        void create() {

        }
    }

}
