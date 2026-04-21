package org.ies.fenix.server.controller;


import dto.teaser.TeaserResponseDTO;
import org.ies.fenix.server.services.TeaserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/teasers")
public class TeaserController {

    @Autowired
    private TeaserService teaserService;

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<TeaserResponseDTO>> getByGameId(
            @PathVariable Integer gameId,
            @RequestParam(required = false) String type
    ) {
        try {
            if (type != null) {
                return ResponseEntity.ok(teaserService.getByGameIdAndType(gameId, type));
            }

            return ResponseEntity.ok(teaserService.getByGameId(gameId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}