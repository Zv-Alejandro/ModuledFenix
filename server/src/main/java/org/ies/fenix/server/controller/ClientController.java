package org.ies.fenix.server.controller;

import org.ies.fenix.controller.dto.client.*;
import org.ies.fenix.server.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.ies.fenix.controller.IClientController;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClientController implements IClientController {

    @Autowired
    private ClientService clientService;

    public ResponseEntity<RegisterResponseDTO> register(ClientRegisterDTO dto) {
        if (dto == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(clientService.register(dto));
    }


    public ResponseEntity<LoginResponseDTO> login(ClientLoginDTO dto) {
        if (dto == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(clientService.login(dto));
    }

    public ResponseEntity<Void> logout(String authorization) {
        String token = extractBearerToken(authorization);
        if (token != null) {
            clientService.logout(token);
        }
        return ResponseEntity.ok().build();
    }

    private String extractBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring(7);
    }

    @Override
    public ResponseEntity<ClientNameDTO> getUsername(String authorization) {
        String token = extractBearerToken(authorization);
        if (token != null) {
            return ResponseEntity.ok( new ClientNameDTO(clientService.getClient(token).getUsername()));
        }
        return ResponseEntity.badRequest().build();
    }

}