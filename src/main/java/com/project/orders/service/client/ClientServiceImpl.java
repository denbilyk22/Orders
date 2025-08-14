package com.project.orders.service.client;

import com.project.orders.dto.request.ClientRequest;
import com.project.orders.dto.response.ClientResponse;
import com.project.orders.dto.response.PageResponse;
import com.project.orders.exception.ApiException;
import com.project.orders.mapper.ClientMapper;
import com.project.orders.persistence.model.Client;
import com.project.orders.persistence.repository.ClientRepository;
import com.project.orders.persistence.specification.ClientSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional(readOnly = true)
    @Override
    public ClientResponse getById(UUID id) {
        var client = findClientById(id);
        return clientMapper.toDto(client);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ClientResponse> getAll(String search, BigDecimal profitFrom, BigDecimal profitTo, Pageable pageable) {
        if (Objects.nonNull(profitFrom) && Objects.nonNull(profitTo)
                && profitFrom.compareTo(profitTo) > 0) {
            throw new ApiException(400, "Profit from must be greater than or equal to profit to");
        }

        var specification = ClientSpecification.get(search, profitFrom, profitTo);
        var page = clientRepository.findAll(specification, pageable);
        return clientMapper.toPageResponse(page);
    }

    @Override
    public ClientResponse create(ClientRequest clientRequest) {
        if (clientRepository.existsByEmail(clientRequest.email())) {
            throw new ApiException(409, "Client already exists");
        }

        var client = clientMapper.toEntity(clientRequest);
        clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    @Override
    public ClientResponse update(UUID id, ClientRequest clientRequest) {
        var client = findClientById(id);
        clientMapper.update(client, clientRequest);
        clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    @Override
    public ClientResponse setActive(UUID id, boolean active) {
        var client = findClientById(id);
        client.setActive(active);

        if (!active) {
            client.setDeactivationDate(ZonedDateTime.now());
        }

        clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    private Client findClientById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "Client not found by id"));
    }

}
