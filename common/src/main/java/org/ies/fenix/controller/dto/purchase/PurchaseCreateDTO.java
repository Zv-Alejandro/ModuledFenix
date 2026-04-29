package org.ies.fenix.controller.dto.purchase;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseCreateDTO {
    private Integer clientId;
    private Integer gameId;
}