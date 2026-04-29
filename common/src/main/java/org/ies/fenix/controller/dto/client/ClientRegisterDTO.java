package org.ies.fenix.controller.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientRegisterDTO {
    private String username;
    private String email;
    private String password;
}