package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.models.LeagueSummoner

interface LeagueSummonerRepository {
    fun existsLeagueSummonerBySummoner(summonerId: String): Boolean
    fun existsLeagueSummonerBySummonerAndLeague(summonerId: String, leagueId: String): Boolean
    fun insertLeagueSummoners(leagueSummoners: List<LeagueSummoner>)
    fun insertLeagueSummoner(leagueSummoner: LeagueSummoner)
    fun updateLeagueSummonerBySummoner(leagueSummoner: LeagueSummoner)
    fun syncLeagueSummoner(leagueSummoner: LeagueSummoner)
    fun getLeagueSummonerBySummonerId(summonerId: String): Array<LeagueSummoner>
}
