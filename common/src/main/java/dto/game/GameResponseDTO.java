package dto.game;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
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