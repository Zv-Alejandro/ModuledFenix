package dto.purchase;

import lombok.Data;

@Data
public class PurchaseCreateDTO {
    private Integer clientId;
    private Integer gameId;
}