package com.code.paratour.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.code.paratour.model.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

}
