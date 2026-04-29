package org.ies.fenix.controller.dto.client;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientRegisterDTO {
    private String username;
    private String email;
    private String password;
}