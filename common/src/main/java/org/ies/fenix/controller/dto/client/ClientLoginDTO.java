package org.ies.fenix.controller.dto.client;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientLoginDTO {
    private String username;     // username o email
    private String password;
}