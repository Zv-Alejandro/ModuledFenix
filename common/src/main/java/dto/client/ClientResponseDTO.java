package dto.client;

import lombok.Data;

@Data
public class ClientResponseDTO {
    private Integer id;
    private String username;
    private String email;
    private String bio;
}