package org.ies.fenix.controller.dto.client;

import org.ies.fenix.controller.dto.ServerResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDTO extends ServerResponseDTO {
    private boolean access;
}
