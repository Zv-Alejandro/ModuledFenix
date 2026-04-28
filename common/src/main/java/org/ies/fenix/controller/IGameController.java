package org.ies.fenix.controller;

import dto.game.GameResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/api/games")
public interface IGameController {

    @GetExchange
    ResponseEntity<List<GameResponseDTO>> getGames(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer devId,
            @RequestParam(required = false) Integer tagId
    );

    @GetExchange("/{id}")
    ResponseEntity<GameResponseDTO> getById(@PathVariable Integer id);
}