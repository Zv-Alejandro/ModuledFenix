package org.ies.fenix.controller.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameSearchDTO {
    private String title;
    private String developerName;
    private List<String> tagNames;
    private Integer limit;
}
