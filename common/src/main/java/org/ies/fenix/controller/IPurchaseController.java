package org.ies.fenix.controller;

import dto.purchase.DownloadResponseDTO;
import dto.purchase.LibraryGameDTO;
import dto.purchase.PurchaseCreateDTO;
import dto.purchase.PurchaseResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange("/api/purchases")
public interface IPurchaseController {

    @PostExchange("")
    ResponseEntity<?> createPurchase(@RequestBody PurchaseCreateDTO dto);

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