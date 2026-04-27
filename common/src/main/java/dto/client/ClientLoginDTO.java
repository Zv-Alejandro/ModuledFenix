package dto.client;

import lombok.Data;

@Data
public class ClientLoginDTO {
    private String username;     // username o email
    private String password;
}