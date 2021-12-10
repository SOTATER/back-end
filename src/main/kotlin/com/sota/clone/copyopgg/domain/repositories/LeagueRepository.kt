package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.League

interface LeagueRepository {
    fun save(league: League)
    fun findById(id: String): League?
}