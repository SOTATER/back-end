package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.models.League

interface LeagueRepository {
    fun insertLeague(league: League): Int
    fun insertLeagues(leagues: List<League>)
    fun updateLeagueById(league: League)
    fun syncLeague(league: League)
    fun findLeagueById(leagueId: String): League?
    fun existsLeagueById(leagueId: String): Boolean
}