package com.project.orders.service.client;

import com.project.orders.dto.request.ClientRequest;
import com.project.orders.dto.response.ClientResponse;
import com.project.orders.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface ClientService {
    ClientResponse getById(UUID id);
    PageResponse<ClientResponse> getAll(String search, BigDecimal profitFrom, BigDecimal profitTo, Pageable pageable);
    ClientResponse create(ClientRequest clientRequest);
    ClientResponse update(UUID id, ClientRequest clientRequest);
    ClientResponse setActive(UUID id, boolean active);
}
