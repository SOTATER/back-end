package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.models.League

interface LeagueRepository {
    fun save(league: League)
    fun findById(id: String): League?
}