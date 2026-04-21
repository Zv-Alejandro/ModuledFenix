package dto.client;

import lombok.Data;

@Data
public class ClientRegisterDTO {
    private String username;
    private String email;
    private String password;
}