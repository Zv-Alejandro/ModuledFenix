package org.ies.fenix.server.services;

import dto.ServerResponseDTO;
import dto.client.*;
import org.ies.fenix.server.models.Client;
import org.ies.fenix.server.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public ServerResponseDTO register(ClientRegisterDTO dto) {
        if (clientRepository.findByUsername(dto.getUsername()).isPresent()) {
            return aResponseRegister("This username already exists", "WARN", false);
        }
        if (clientRepository.findByEmail(dto.getEmail()).isPresent()) {
            return aResponseRegister("This email is already linked to an account", "WARN", false);
        }
        if (dto.getEmail().isEmpty()) {
            return aResponseRegister("No email attached", "ERROR", false);
        }
        if (dto.getUsername().isBlank()) {
            return aResponseRegister("No username attached", "ERROR", false);
        }

        Client client = new Client();
        client.setUsername(dto.getUsername());
        client.setEmail(dto.getEmail());
        client.setPasswordHashed(passwordEncoder.encode(dto.getPassword()));
        client.setBio(null);
        clientRepository.save(client);

        return aResponseRegister("User registered successfully", "OK", true);
    }

    private RegisterResponseDTO aResponseRegister(String message, String status, boolean access) {
        return RegisterResponseDTO.builder()
                .status(status)
                .message(message)
                .access(access)
                .build();
    }

    public LoginResponseDTO login(ClientLoginDTO dto) {
        Optional<Client> clientOpt = clientRepository.findByUsername(dto.getUsername());

        if (clientOpt.isEmpty()) {
            return LoginResponseDTO.builder()
                    .status("WARN")
                    .message("Password incorrect")
                    .build();
        }

        Client client = clientOpt.get();

        if (!passwordEncoder.matches(dto.getPassword(), client.getPasswordHashed())) {
            return LoginResponseDTO.builder()
                    .status("WARN")
                    .message("Password incorrect")
                    .build();
        }

        String token = tokenService.generateToken(client.getId());

        return LoginResponseDTO.builder()
                .status("OK")
                .message("Login successful")
                .clientId(client.getId())
                .username(client.getUsername())
                .token(token)
                .build();
    }

    public boolean logout(String token) {
        tokenService.revoke(token);
        return true;
    }
}