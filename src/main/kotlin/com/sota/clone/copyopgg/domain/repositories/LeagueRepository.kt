package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.League
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LeagueRepository : JpaRepository<League, String> {
}