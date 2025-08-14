package com.project.orders.service.clientbalance;

import com.project.orders.persistence.repository.ClientBalanceChangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientBalanceChangeServiceImpl implements ClientBalanceChangeService {

    private final ClientBalanceChangeRepository clientBalanceChangeRepository;

    @Override
    public void refreshProfitForAllClients() {
        clientBalanceChangeRepository.refreshProfitForAllClients();
    }

    @Override
    public void refreshProfitForClient(UUID clientId) {
        if (!clientBalanceChangeRepository.existsByClientId(clientId)) {
            return;
        }

        clientBalanceChangeRepository.refreshProfitForClient(clientId);
    }
}
