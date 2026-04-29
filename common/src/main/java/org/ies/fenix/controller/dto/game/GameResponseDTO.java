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
    private BigDecimal tamanoMb;
    private Integer downloads;
    private BigDecimal price;
    private Integer devId;
    private String devUsername;
    private List<String> tags;
}