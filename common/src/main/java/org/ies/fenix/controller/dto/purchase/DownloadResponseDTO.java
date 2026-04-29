package org.ies.fenix.controller.dto.purchase;

import lombok.Data;

@Data
public class DownloadResponseDTO {
    private Integer gameId;
    private String title;
    private Integer downloads;
}