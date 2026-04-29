package org.ies.fenix.controller;

import org.ies.fenix.controller.dto.client.ClientLoginDTO;
import org.ies.fenix.controller.dto.client.ClientRegisterDTO;
import org.ies.fenix.controller.dto.client.LoginResponseDTO;
import org.ies.fenix.controller.dto.client.RegisterResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api/clients")
public interface IClientController {

    @PostExchange("/register")
    ResponseEntity<RegisterResponseDTO> register(@RequestBody ClientRegisterDTO dto);

    @PostExchange("/login")
    ResponseEntity<LoginResponseDTO> login(@RequestBody ClientLoginDTO dto);

    @PostExchange("/logout")
    ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization);
}