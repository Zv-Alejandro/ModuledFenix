package org.ies.fenix.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.client.ClientLoginDTO;
import dto.client.ClientRegisterDTO;
import dto.client.LoginResponseDTO;
import dto.client.RegisterResponseDTO;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ClientApiService {

    private static final String BASE_URL = "http://localhost:8080/api/clients";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ClientApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public LoginResponseDTO login(String username, String password) {
        ClientLoginDTO dto = new ClientLoginDTO();
        dto.setUsername(username);
        dto.setPassword(password);

        try {
            String json = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/login"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Login request failed with status: " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), LoginResponseDTO.class);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Could not connect to backend login endpoint", e);
        }
    }

    public RegisterResponseDTO register(String username, String email, String password) {
        ClientRegisterDTO dto = new ClientRegisterDTO();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword(password);

        try {
            String json = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/register"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Register request failed with status: " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), RegisterResponseDTO.class);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Could not connect to backend register endpoint", e);
        }
    }
}