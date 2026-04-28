package org.ies.fenix.controller;

import dto.client.ClientLoginDTO;
import dto.client.ClientRegisterDTO;
import dto.client.LoginResponseDTO;
import dto.client.RegisterResponseDTO;
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