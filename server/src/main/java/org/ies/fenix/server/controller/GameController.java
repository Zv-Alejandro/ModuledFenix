package org.ies.fenix.server.controller;

import dto.game.GameResponseDTO;
import org.ies.fenix.server.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping("")
    public ResponseEntity<List<GameResponseDTO>> getGames(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer devId,
            @RequestParam(required = false) Integer tagId
    ) {
        try {
            if (title != null) {
                return ResponseEntity.ok(gameService.getByTitle(title));
            }

            if (devId != null) {
                return ResponseEntity.ok(gameService.getByDevId(devId));
            }

            if (tagId != null) {
                return ResponseEntity.ok(gameService.getByTagId(tagId));
            }

            return ResponseEntity.ok(gameService.getAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDTO> getById(@PathVariable Integer id) {
        try {
            GameResponseDTO response = gameService.getById(id);
            if (response == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}