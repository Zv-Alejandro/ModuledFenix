package org.ies.fenix.server.services;

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

    public Optional<Client> findByUsername(String username) {
        return clientRepository.findByUsername(username);
    }

    public Client saveClient(String username, String passwordHashed) {
        Client client = new Client();
        client.setUsername(username);
        client.setEmail(null);
        client.setPasswordHashed(passwordHashed);
        client.setBio(null);
        return clientRepository.save(client);
    }

    public ClientResponseDTO register(ClientRegisterDTO dto) {
        if (clientRepository.findByUsername(dto.getUsername()).isPresent()) {
            return null;
        }

        if (clientRepository.findByEmail(dto.getEmail()).isPresent()) {
            return null;
        }


        Client client = new Client();
        client.setUsername(dto.getUsername());
        client.setEmail(dto.getEmail());
        client.setPasswordHashed(passwordEncoder.encode(dto.getPassword()));
        client.setBio(null);
        Client saved = clientRepository.save(client);
        return toResponseDTO(saved);
    }

    public LoginResponseDTO login(ClientLoginDTO dto) {
        Client client = findByLogin(dto.getLogin());

        if (client == null) {
            return new LoginResponseDTO(-2, "Usuario no existe");
        }


        boolean passwordCorrecta = passwordEncoder.matches(dto.getPassword(), client.getPasswordHashed());


        if (passwordCorrecta) {
            clientRepository.save(client);
            return new LoginResponseDTO(1, "Login correcto");
        }


        clientRepository.save(client);
        return new LoginResponseDTO(-1, "Password incorrecta");
    }

    public ClientResponseDTO getById(Integer id) {
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) {
            return null;
        }
        return toResponseDTO(client);
    }

    public ClientResponseDTO getByUsername(String username) {
        Client client = clientRepository.findByUsername(username).orElse(null);
        if (client == null) {
            return null;
        }
        return toResponseDTO(client);
    }

    public ClientResponseDTO update(Integer id, ClientUpdateDTO dto) {
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) {
            return null;
        }


        if (dto.getEmail() != null && !dto.getEmail().equals(client.getEmail())) {
            Optional<Client> existingEmail = clientRepository.findByEmail(dto.getEmail());
            if (existingEmail.isPresent()) {
                return null;
            }
            client.setEmail(dto.getEmail());
        }

        client.setBio(dto.getBio());

        Client updated = clientRepository.save(client);
        return toResponseDTO(updated);
    }

    public boolean delete(Integer id) {
        if (!clientRepository.existsById(id)) {
            return false;
        }
        clientRepository.deleteById(id);
        return true;
    }

//    public ClientResponseDTO verifyEmail(Integer id) {
//        Client client = clientRepository.findById(id).orElse(null);
//        if (client == null) {
//            return null;
//        }
//
//        client.setEmailVerificado(true);
//        client.setFechaVerificado(LocalDate.now());
//
//        Client updated = clientRepository.save(client);
//        return toResponseDTO(updated);
//    }

    private Client findByLogin(String login) {
        if (login == null) {
            return null;
        }

        if (login.contains("@")) {
            return clientRepository.findByEmail(login).orElse(null);
        }

        return clientRepository.findByUsername(login).orElse(null);
    }

    private ClientResponseDTO toResponseDTO(Client client) {
        ClientResponseDTO dto = new ClientResponseDTO();
        dto.setId(client.getId());
        dto.setUsername(client.getUsername());
        dto.setEmail(client.getEmail());
        dto.setBio(client.getBio());
        return dto;
    }
}