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
    public ResponseEntity<ClientResponseDTO> register(@RequestBody ClientRegisterDTO dto) {
        try {
            ClientResponseDTO response = clientService.register(dto);
            if (response == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody ClientLoginDTO dto) {
        try {
            LoginResponseDTO response = clientService.login(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getById(@PathVariable Integer id) {
        try {
            ClientResponseDTO response = clientService.getById(id);
            if (response == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ClientResponseDTO> getByUsername(@PathVariable String username) {
        try {
            ClientResponseDTO response = clientService.getByUsername(username);
            if (response == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable Integer id,
                                                    @RequestBody ClientUpdateDTO dto) {
        try {
            ClientResponseDTO response = clientService.update(id, dto);
            if (response == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            boolean deleted = clientService.delete(id);
            if (!deleted) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

//    @PatchMapping("/{id}/verify-email")
//    public ResponseEntity<ClientResponseDTO> verifyEmail(@PathVariable Integer id) {
//        try {
//            ClientResponseDTO response = clientService.verifyEmail(id);
//            if (response == null) {
//                return ResponseEntity.notFound().build();
//            }
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
}