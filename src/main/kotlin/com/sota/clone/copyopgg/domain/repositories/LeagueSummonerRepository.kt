package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.models.LeagueSummoner

interface LeagueSummonerRepository {
    fun existsLeagueSummonerBySummoner(summonerId: String): Boolean
    fun insertLeagueSummoners(leagueSummoners: List<LeagueSummoner>)
    fun insertLeagueSummoner(leagueSummoner: LeagueSummoner)
    fun updateLeagueSummonerBySummoner(leagueSummoner: LeagueSummoner)
    fun syncLeagueSummoner(leagueSummoner: LeagueSummoner)
    fun getLeagueSummonerBySummonerId(summonerId: String): LeagueSummoner?
}
