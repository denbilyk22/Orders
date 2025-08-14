package com.project.orders.service.clientbalance;

import java.util.UUID;

public interface ClientBalanceChangeService {
    void refreshProfitForAllClients();
    void refreshProfitForClient(UUID clientId);
}
