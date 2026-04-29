package org.ies.fenix.server.controller;

import org.ies.fenix.controller.dto.game.GameResponseDTO;
import org.ies.fenix.server.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.ies.fenix.controller.IGameController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController implements IGameController {

    @Autowired
    private GameService gameService;

    public ResponseEntity<List<GameResponseDTO>> getGames(
            String title,
            Integer devId,
            Integer tagId
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

    public ResponseEntity<GameResponseDTO> getById(Integer id) {
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