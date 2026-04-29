package org.ies.fenix.controller.dto.tag;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagResponseDTO {
    private Integer id;
    private String name;
    private String description;
}