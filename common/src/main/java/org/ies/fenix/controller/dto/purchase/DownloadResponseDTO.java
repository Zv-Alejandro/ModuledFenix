package org.ies.fenix.controller.dto.purchase;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DownloadResponseDTO {
    private Integer gameId;
    private String title;
    private Integer downloads;
}