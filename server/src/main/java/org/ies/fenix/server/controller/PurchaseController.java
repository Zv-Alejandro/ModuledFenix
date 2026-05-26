package org.ies.fenix.server.controller;

import org.ies.fenix.controller.IPurchaseController;
import org.ies.fenix.controller.dto.purchase.LibraryGameDTO;
import org.ies.fenix.controller.dto.purchase.PurchaseCreateDTO;
import org.ies.fenix.controller.dto.purchase.PurchaseResponseDTO;
import org.ies.fenix.server.exception.AlreadyPurchasedException;
import org.ies.fenix.server.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PurchaseController implements IPurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @Override
    public ResponseEntity<?> createPurchase(String authorization, PurchaseCreateDTO dto) {
        try {
            PurchaseResponseDTO response = purchaseService.createPurchase(dto);

            if (response == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(response);

        } catch (AlreadyPurchasedException e) {
            return ResponseEntity.status(409).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<LibraryGameDTO>> getLibraryByClientId(String authorization,
                                                                     Integer clientId) {
        try {
            return ResponseEntity.ok(purchaseService.getLibraryByClientId(clientId));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Boolean> hasPurchased(@RequestHeader String authorization,
                                                @RequestParam Integer clientId,
                                                @RequestParam Integer gameId) {
        try {
            boolean purchased = purchaseService.hasPurchased(clientId, gameId);
            return ResponseEntity.ok(purchased);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(false);
        }
    }
}