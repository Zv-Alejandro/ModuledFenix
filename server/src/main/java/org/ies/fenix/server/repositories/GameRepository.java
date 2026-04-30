package org.ies.fenix.server.repositories;

import org.ies.fenix.server.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    List<Game> findByTitleContainingIgnoreCase(String title);
    List<Game> findByDevId(Integer devId);
    List<Game> findByTagsId(Integer tagId);
    @Query("""
        SELECT g
        FROM Game g
        JOIN g.tags t
        WHERE t.name IN :names
        GROUP BY g
        HAVING COUNT(DISTINCT t.name) = :size
    """)
    List<Game> findByAllTagNames(@Param("names") List<String> names,
                                 @Param("size") long size);
    List<Game> findByDev_Username(String username);
}