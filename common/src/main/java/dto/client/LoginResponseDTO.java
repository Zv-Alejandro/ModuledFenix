package dto.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import dto.ServerResponseDTO;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO extends ServerResponseDTO {
    private Integer clientId;
    private String username;
    private String token;
}
