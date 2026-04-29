package org.ies.fenix.controller.dto.teaser;

import lombok.Data;

@Data
public class TeaserResponseDTO {
    private Integer id;
    private Integer gameId;
    private String fileName;
    private String type;
}