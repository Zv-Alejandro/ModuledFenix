package org.ies.fenix.controller;

import dto.teaser.TeaserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/api/teasers")
public interface ITeaserController {

    @GetExchange("/game/{gameId}")
    ResponseEntity<List<TeaserResponseDTO>> getByGameId(
            @PathVariable Integer gameId,
            @RequestParam(required = false) String type
    );
}