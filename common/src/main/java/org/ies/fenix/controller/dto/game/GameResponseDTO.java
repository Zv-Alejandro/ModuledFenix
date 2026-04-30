package org.ies.fenix.controller.dto.game;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private String sizeApproximation;
    private String downloadsApproximation;
    private BigDecimal price;
    private String devUsername;
    private List<String> tags;
}