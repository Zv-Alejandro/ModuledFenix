package org.ies.fenix.controller.dto.purchase;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LibraryGameDTO {
    private Integer gameId;
    private String title;
    private String description;
    private BigDecimal tamanoMb;
    private Integer downloads;
    private BigDecimal price;
}