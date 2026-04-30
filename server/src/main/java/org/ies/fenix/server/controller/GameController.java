package org.ies.fenix.server.controller;

import org.ies.fenix.controller.dto.game.GameResponseDTO;
import org.ies.fenix.controller.dto.game.GameSearchDTO;
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

    @Override
    public ResponseEntity<List<GameResponseDTO>> getManyGames(GameSearchDTO dto) {
        try {
            if (dto == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(gameService.getGames(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<GameResponseDTO> getById(Integer id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(gameService.getGameById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}