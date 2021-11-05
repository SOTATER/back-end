package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.models.LeagueSummoner

interface LeagueSummonerRepository {
    fun save(leagueSummoner: LeagueSummoner)
    fun findById(summonerId: String, leagueId: String): LeagueSummoner?
    fun findBySummonerId(summonerId: String): List<LeagueSummoner>
}
