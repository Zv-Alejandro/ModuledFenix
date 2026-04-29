package org.ies.fenix.controller.dto.purchase;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseResponseDTO {
    private Integer id;
    private Integer clientId;
    private Integer gameId;
    private String gameTitle;
    private BigDecimal price;
}