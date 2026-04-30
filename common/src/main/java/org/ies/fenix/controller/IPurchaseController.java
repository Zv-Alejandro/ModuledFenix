package org.ies.fenix.controller;


import org.ies.fenix.controller.dto.purchase.DownloadResponseDTO;
import org.ies.fenix.controller.dto.purchase.LibraryGameDTO;
import org.ies.fenix.controller.dto.purchase.PurchaseCreateDTO;
import org.ies.fenix.controller.dto.purchase.PurchaseResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange("/api/purchases")
public interface IPurchaseController {

    @PostExchange
    ResponseEntity<?> createPurchase(
            @RequestBody PurchaseCreateDTO dto);

    @GetExchange("/client/{clientId}")
    ResponseEntity<List<PurchaseResponseDTO>> getByClientId(
            @PathVariable Integer clientId
    );

    @GetExchange("/client/{clientId}/library")
    ResponseEntity<List<LibraryGameDTO>> getLibraryByClientId(
            @PathVariable Integer clientId
    );

    @GetExchange("/client/{clientId}/download/{gameId}")
    ResponseEntity<DownloadResponseDTO> downloadGame(
            @PathVariable Integer clientId,
            @PathVariable Integer gameId
    );
}