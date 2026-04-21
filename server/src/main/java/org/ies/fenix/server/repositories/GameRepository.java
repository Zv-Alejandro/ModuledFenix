package org.ies.fenix.server.repositories;

import org.ies.fenix.server.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    List<Game> findByTitleContainingIgnoreCase(String title);
    List<Game> findByDevId(Integer devId);
    List<Game> findByTagsId(Integer tagId);
}