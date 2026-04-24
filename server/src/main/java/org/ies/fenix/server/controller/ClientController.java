package org.ies.fenix.server.controller;

import dto.client.*;
import org.ies.fenix.server.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody ClientRegisterDTO dto) {
        return ResponseEntity.ok(clientService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody ClientLoginDTO dto) {
        return ResponseEntity.ok(clientService.login(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization) {
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
}