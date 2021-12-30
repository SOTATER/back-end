package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.Match
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MatchRepository : JpaRepository<Match, String> {
    @Query(
        value = "SELECT * FROM \"matches\" " +
                "ORDER BY \"game_creation\" DESC LIMIT 1",
        nativeQuery = true
    )
    fun findLatestMatch(): Match
}