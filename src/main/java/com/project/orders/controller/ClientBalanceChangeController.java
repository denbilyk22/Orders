package com.project.orders.controller;

import com.project.orders.exception.ExceptionResponse;
import com.project.orders.service.clientbalance.ClientBalanceChangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/client-balance")
@RequiredArgsConstructor
public class ClientBalanceChangeController {

    private final ClientBalanceChangeService clientBalanceChangeService;

    @PutMapping("/refresh-all")
    @Operation(summary = "Refresh profit for all clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profit refreshed"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))

    })
    public void refreshProfitForAllClients() {
        clientBalanceChangeService.refreshProfitForAllClients();
    }

    @PutMapping("/{clientId}/refresh")
    @Operation(summary = "Refresh profit for client by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profit refreshed"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public void refreshProfitForClient(@PathVariable UUID clientId) {
        clientBalanceChangeService.refreshProfitForClient(clientId);
    }


}
