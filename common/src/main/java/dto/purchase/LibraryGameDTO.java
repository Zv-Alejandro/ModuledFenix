package dto.purchase;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LibraryGameDTO {
    private Integer gameId;
    private String title;
    private String description;
    private BigDecimal tamanoMb;
    private Integer downloads;
    private BigDecimal price;
}