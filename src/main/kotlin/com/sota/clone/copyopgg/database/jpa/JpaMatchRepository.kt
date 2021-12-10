package com.sota.clone.copyopgg.database.jpa

import com.sota.clone.copyopgg.domain.entities.Match
import org.springframework.data.jpa.repository.JpaRepository

interface JpaMatchRepository: JpaRepository<Match, String> {
}