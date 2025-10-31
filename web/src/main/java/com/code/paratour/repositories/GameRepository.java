package com.code.paratour.repositories;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.code.paratour.model.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
@EntityGraph(attributePaths = {
    "phases", 
    "phases.enigmas"
})
@Query("SELECT g FROM Game g WHERE g.id = :id")
Game findGameWithPhasesAndEnigmas(@Param("id") Long id);



}
