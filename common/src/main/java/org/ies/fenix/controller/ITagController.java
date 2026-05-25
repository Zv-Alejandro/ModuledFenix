package org.ies.fenix.controller;

import org.ies.fenix.controller.dto.tag.TagResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/api/tags")
public interface ITagController {

    @GetExchange
    ResponseEntity<List<TagResponseDTO>> getAll(
            @RequestHeader("Authorization") String authorization
    );

    @GetExchange("/names")
    ResponseEntity<List<String>> getNames(
            @RequestHeader("Authorization") String authorization
    );

    @GetExchange("/{id}")
    ResponseEntity<TagResponseDTO> getById(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id
    );
}