package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.Match
import org.springframework.data.jpa.repository.JpaRepository

interface MatchRepository: JpaRepository<Match, String> {
}