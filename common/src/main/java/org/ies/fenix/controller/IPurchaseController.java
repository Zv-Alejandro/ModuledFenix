package org.ies.fenix.controller;

import org.ies.fenix.controller.dto.purchase.LibraryGameDTO;
import org.ies.fenix.controller.dto.purchase.PurchaseCreateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange("/api/purchases")
public interface IPurchaseController {

    @PostExchange
    ResponseEntity<?> createPurchase(
            @RequestHeader String authorization,
            @RequestBody PurchaseCreateDTO dto
    );

    @GetExchange("/client/{clientId}/library")
    ResponseEntity<List<LibraryGameDTO>> getLibraryByClientId(
            @RequestHeader String authorization,
            @PathVariable Integer clientId
    );

    @GetExchange("/hasPurchased")
    ResponseEntity<Boolean> hasPurchased(
            @RequestHeader String authorization,
            @RequestParam Integer clientId,
            @RequestParam Integer gameId
    );
}