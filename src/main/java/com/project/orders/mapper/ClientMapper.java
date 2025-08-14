package com.project.orders.mapper;

import com.project.orders.dto.request.ClientRequest;
import com.project.orders.dto.response.ClientResponse;
import com.project.orders.dto.response.PageResponse;
import com.project.orders.persistence.model.Client;
import com.project.orders.persistence.model.ClientBalanceChange;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    Client toEntity(ClientRequest clientRequest);

    void update(@MappingTarget Client client, ClientRequest clientRequest);

    @Mapping(target = "profit", source = "balanceChanges", qualifiedByName = "calculateProfit")
    ClientResponse toDto(Client client);

    List<ClientResponse> toDtos(List<Client> client);

    default PageResponse<ClientResponse> toPageResponse(Page<Client> page) {
        return PageResponse.<ClientResponse>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(toDtos(page.getContent()))
                .build();
    }

    @Named("calculateProfit")
    default BigDecimal calculateProfit(List<ClientBalanceChange> changes) {
        return Optional.ofNullable(changes)
                .orElse(List.of())
                .stream()
                .map(ClientBalanceChange::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
