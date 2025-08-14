package com.project.orders.service.order;

import com.project.orders.dto.request.OrderRequest;
import com.project.orders.exception.ApiException;
import com.project.orders.mapper.ClientMapper;
import com.project.orders.mapper.OrderMapper;
import com.project.orders.persistence.model.Client;
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
class OrderServiceTest {

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

    @Spy
    private ClientMapper clientMapper = Mappers.getMapper(ClientMapper.class);

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
        void successfully() {
            // given
            var startProcessingTime = ZonedDateTime.now();
            var endProcessingTime = startProcessingTime.plusDays(1);

            var supplierId = UUID.randomUUID();
            var supplier = Client.builder()
                    .id(supplierId)
                    .active(true)
                    .build();

            var consumerId = UUID.randomUUID();
            var consumer = Client.builder()
                    .id(consumerId)
                    .active(true)
                    .build();

            var name = "Order";

            var orderRequest = OrderRequest.builder()
                    .price(BigDecimal.TEN)
                    .name(name)
                    .startProcessingTime(startProcessingTime)
                    .endProcessingTime(endProcessingTime)
                    .supplierId(supplierId)
                    .consumerId(consumerId)
                    .build();

            Mockito.when(orderRepository.isSimilarOrderExist(name, supplierId, consumerId))
                    .thenReturn(false);

            Mockito.when(clientRepository.findById(supplierId))
                    .thenReturn(Optional.of(supplier));

            Mockito.when(clientRepository.findById(consumerId))
                    .thenReturn(Optional.of(consumer));

            Mockito.when(clientBalanceChangeRepository.getClientProfit(consumer.getId()))
                    .thenReturn(BigDecimal.ZERO);

            // when
            var result = orderService.create(orderRequest);

            // then
            Assertions.assertNull(result.id());
            Assertions.assertEquals("Order", result.name());
            Assertions.assertEquals(BigDecimal.TEN, result.price());
            Assertions.assertEquals(startProcessingTime, result.startProcessingTime());
            Assertions.assertEquals(endProcessingTime, result.endProcessingTime());
            Assertions.assertNotNull(result.createdDate());

            Assertions.assertEquals(supplierId, result.supplier().id());
            Assertions.assertEquals(consumerId, result.consumer().id());
        }

        @Test
        void getPriceNotValidException() {
            // given
            var orderRequest = OrderRequest.builder()
                    .price(BigDecimal.valueOf(0))
                    .build();

            // when && then
            Assertions.assertThrows(ApiException.class, () -> orderService.create(orderRequest));
        }

        @Test
        void getProcessingTimesNotValidException() {
            // given
            var startProcessingTime = ZonedDateTime.now();
            var endProcessingTime = startProcessingTime.minusDays(1);

            var orderRequest = OrderRequest.builder()
                    .price(BigDecimal.valueOf(0))
                    .startProcessingTime(startProcessingTime)
                    .endProcessingTime(endProcessingTime)
                    .build();

            // when && then
            Assertions.assertThrows(ApiException.class, () -> orderService.create(orderRequest));
        }

        @Test
        void getSameSupplierAndCustomerException() {
            // given
            var clientId = UUID.randomUUID();

            var orderRequest = OrderRequest.builder()
                    .supplierId(clientId)
                    .consumerId(clientId)
                    .build();

            // when && then
            Assertions.assertThrows(ApiException.class, () -> orderService.create(orderRequest));
        }

        @Test
        void getOrderNotUniqueException() {
            // given
            var supplierId = UUID.randomUUID();
            var consumerId = UUID.randomUUID();

            var name = "Order";

            var orderRequest = OrderRequest.builder()
                    .name(name)
                    .supplierId(supplierId)
                    .consumerId(consumerId)
                    .build();

            Mockito.when(orderRepository.isSimilarOrderExist(name, supplierId, consumerId))
                    .thenReturn(true);

            // when && then
            Assertions.assertThrows(ApiException.class, () -> orderService.create(orderRequest));
        }

        @Test
        void getSupplierNotFoundException() {
            // given
            var supplierId = UUID.randomUUID();
            var consumerId = UUID.randomUUID();

            var name = "Order";

            var orderRequest = OrderRequest.builder()
                    .name(name)
                    .supplierId(supplierId)
                    .consumerId(consumerId)
                    .build();

            Mockito.when(orderRepository.isSimilarOrderExist(name, supplierId, consumerId))
                    .thenReturn(false);
            Mockito.when(clientRepository.findById(supplierId))
                    .thenReturn(Optional.empty());

            // when && then
            Assertions.assertThrows(ApiException.class, () -> orderService.create(orderRequest));
        }

        @Test
        void getConsumerNotFoundException() {
            // given
            var supplierId = UUID.randomUUID();
            var consumerId = UUID.randomUUID();

            var name = "Order";

            var orderRequest = OrderRequest.builder()
                    .name(name)
                    .supplierId(supplierId)
                    .consumerId(consumerId)
                    .build();

            Mockito.when(orderRepository.isSimilarOrderExist(name, supplierId, consumerId))
                    .thenReturn(false);
            Mockito.when(clientRepository.findById(supplierId))
                    .thenReturn(Optional.of(new Client()));
            Mockito.when(clientRepository.findById(supplierId))
                    .thenReturn(Optional.empty());

            // when && then
            Assertions.assertThrows(ApiException.class, () -> orderService.create(orderRequest));
        }

        @Test
        void getSupplierNotActiveException() {
            // given
            var supplierId = UUID.randomUUID();
            var supplier = Client.builder()
                    .id(supplierId)
                    .active(false)
                    .build();

            var consumerId = UUID.randomUUID();
            var consumer = Client.builder()
                    .id(consumerId)
                    .active(true)
                    .build();

            var name = "Order";

            var orderRequest = OrderRequest.builder()
                    .name(name)
                    .supplierId(supplierId)
                    .consumerId(consumerId)
                    .build();

            Mockito.when(orderRepository.isSimilarOrderExist(name, supplierId, consumerId))
                    .thenReturn(false);
            Mockito.when(clientRepository.findById(supplierId))
                    .thenReturn(Optional.of(supplier));
            Mockito.when(clientRepository.findById(supplierId))
                    .thenReturn(Optional.of(consumer));

            // when && then
            Assertions.assertThrows(ApiException.class, () -> orderService.create(orderRequest));
        }

        @Test
        void getConsumerNotActiveException() {
            // given
            var supplierId = UUID.randomUUID();
            var supplier = Client.builder()
                    .id(supplierId)
                    .active(true)
                    .build();

            var consumerId = UUID.randomUUID();
            var consumer = Client.builder()
                    .id(consumerId)
                    .active(false)
                    .build();

            var name = "Order";

            var orderRequest = OrderRequest.builder()
                    .name(name)
                    .supplierId(supplierId)
                    .consumerId(consumerId)
                    .build();

            Mockito.when(orderRepository.isSimilarOrderExist(name, supplierId, consumerId))
                    .thenReturn(false);
            Mockito.when(clientRepository.findById(supplierId))
                    .thenReturn(Optional.of(supplier));
            Mockito.when(clientRepository.findById(supplierId))
                    .thenReturn(Optional.of(consumer));

            // when && then
            Assertions.assertThrows(ApiException.class, () -> orderService.create(orderRequest));
        }

        @Test
        void getConsumerBalanceDecreasedException() {
            // given
            var supplierId = UUID.randomUUID();
            var supplier = Client.builder()
                    .id(supplierId)
                    .active(true)
                    .build();

            var consumerId = UUID.randomUUID();
            var consumer = Client.builder()
                    .id(consumerId)
                    .active(false)
                    .build();

            var name = "Order";

            var orderRequest = OrderRequest.builder()
                    .name(name)
                    .price(BigDecimal.TEN)
                    .supplierId(supplierId)
                    .consumerId(consumerId)
                    .build();

            Mockito.when(orderRepository.isSimilarOrderExist(name, supplierId, consumerId))
                    .thenReturn(false);
            Mockito.when(clientRepository.findById(supplierId))
                    .thenReturn(Optional.of(supplier));
            Mockito.when(clientRepository.findById(supplierId))
                    .thenReturn(Optional.of(consumer));
            Mockito.when(clientBalanceChangeRepository.getClientProfit(consumerId))
                    .thenReturn(BigDecimal.valueOf(-1000));

            // when && then
            Assertions.assertThrows(ApiException.class, () -> orderService.create(orderRequest));
        }

    }

}
