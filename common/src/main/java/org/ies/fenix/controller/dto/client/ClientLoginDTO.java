package org.ies.fenix.controller.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientLoginDTO {
    private String username;     // username o email
    private String password;
}