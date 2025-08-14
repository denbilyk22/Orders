package com.project.orders.controller;

import com.project.orders.dto.request.ClientRequest;
import com.project.orders.dto.response.ClientResponse;
import com.project.orders.dto.response.PageResponse;
import com.project.orders.exception.ExceptionResponse;
import com.project.orders.service.client.ClientService;
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

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    @Operation(summary = "Get client by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client retrieved", content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found by id", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))

    })
    public ClientResponse getClientById(@PathVariable UUID id) {
        return clientService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Get all clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clients retrieved", content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public PageResponse<ClientResponse> getAllClients(@RequestParam(required = false) String search,
                                                      @RequestParam(required = false) BigDecimal profitFrom,
                                                      @RequestParam(required = false) BigDecimal profitTo,
                                                      Pageable pageable) {
        return clientService.getAll(search, profitFrom, profitTo, pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client created", content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Client request not valid", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ClientResponse createClient(@RequestBody @Valid ClientRequest clientRequest) {
        return clientService.create(clientRequest);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client created", content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Client request not valid", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ClientResponse updateClient(@PathVariable UUID id, @RequestBody @Valid ClientRequest clientRequest) {
        return clientService.update(id, clientRequest);
    }

    @PutMapping("/{id}/active")
    @Operation(summary = "Set active to client client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client created", content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ClientResponse updateClient(@PathVariable UUID id, @RequestParam boolean active) {
        return clientService.setActive(id, active);
    }


}
