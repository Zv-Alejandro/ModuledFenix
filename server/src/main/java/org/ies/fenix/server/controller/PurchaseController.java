package org.ies.fenix.server.controller;

import dto.purchase.DownloadResponseDTO;
import dto.purchase.LibraryGameDTO;
import dto.purchase.PurchaseCreateDTO;
import dto.purchase.PurchaseResponseDTO;
import org.ies.fenix.controller.IPurchaseController;
import org.ies.fenix.server.exception.AlreadyPurchasedException;
import org.ies.fenix.server.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PurchaseController implements IPurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    public ResponseEntity<?> createPurchase(PurchaseCreateDTO dto) {
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

    public ResponseEntity<List<PurchaseResponseDTO>> getByClientId(Integer clientId) {
        try {
            return ResponseEntity.ok(purchaseService.getByClientId(clientId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<List<LibraryGameDTO>> getLibraryByClientId(Integer clientId) {
        try {
            return ResponseEntity.ok(purchaseService.getLibraryByClientId(clientId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<DownloadResponseDTO> downloadGame(Integer clientId, Integer gameId) {
        try {
            DownloadResponseDTO response = purchaseService.downloadGame(clientId, gameId);
            if (response == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}