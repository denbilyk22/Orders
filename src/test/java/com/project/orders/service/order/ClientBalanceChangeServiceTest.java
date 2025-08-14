package com.project.orders.service.order;

import com.project.orders.persistence.repository.ClientBalanceChangeRepository;
import com.project.orders.service.clientbalance.ClientBalanceChangeServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientBalanceChangeServiceTest {

    @InjectMocks
    private ClientBalanceChangeServiceImpl clientBalanceChangeService;

    @Mock
    private ClientBalanceChangeRepository clientBalanceChangeRepository;

    @Nested
    class RefreshProfitForAllClientsTest {

        @Test
        void successfully() {
            // when && then
            assertDoesNotThrow(() -> clientBalanceChangeService.refreshProfitForAllClients());

            verify(clientBalanceChangeRepository).refreshProfitForAllClients();
        }

        @Test
        void getException() {
            // given
            doThrow(RuntimeException.class)
                    .when(clientBalanceChangeRepository).refreshProfitForAllClients();

            // when && then
            assertThrows(RuntimeException.class, () -> clientBalanceChangeService.refreshProfitForAllClients());

            verify(clientBalanceChangeRepository).refreshProfitForAllClients();
        }
    }

    @Nested
    class RefreshProfitForClientTest {

        private final UUID clientId = UUID.randomUUID();

        @Test
        void successfully() {
            // given
            when(clientBalanceChangeRepository.existsByClientId(clientId))
                    .thenReturn(true);

            // when && then
            assertDoesNotThrow(() -> clientBalanceChangeService.refreshProfitForClient(clientId));

            verify(clientBalanceChangeRepository).existsByClientId(clientId);
            verify(clientBalanceChangeRepository).refreshProfitForClient(clientId);
        }

        @Test
        void successfullyWithoutProfitRefreshRepositoryCall() {
            // given
            when(clientBalanceChangeRepository.existsByClientId(clientId))
                    .thenReturn(false);

            // when && then
            assertDoesNotThrow(() -> clientBalanceChangeService.refreshProfitForClient(clientId));

            verify(clientBalanceChangeRepository).existsByClientId(clientId);
            verify(clientBalanceChangeRepository, never()).refreshProfitForClient(clientId);
        }

        @Test
        void getExceptionWhenCheckingClientExistence() {
            // given
            when(clientBalanceChangeRepository.existsByClientId(clientId))
                    .thenThrow(RuntimeException.class);

            // when && then
            assertThrows(RuntimeException.class, () -> clientBalanceChangeService.refreshProfitForClient(clientId));
        }

        @Test
        void getExceptionWhenRefreshingClientProfit() {
            // given
            when(clientBalanceChangeRepository.existsByClientId(clientId))
                    .thenReturn(true);
            doThrow(RuntimeException.class)
                    .when(clientBalanceChangeRepository).refreshProfitForClient(clientId);

            // when && then
            assertThrows(RuntimeException.class, () -> clientBalanceChangeService.refreshProfitForClient(clientId));
        }

    }

}
