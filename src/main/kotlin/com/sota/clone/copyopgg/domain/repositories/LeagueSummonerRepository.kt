package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.LeagueSummoner
import com.sota.clone.copyopgg.domain.entities.LeagueSummonerPK

interface LeagueSummonerRepository {
    fun save(leagueSummoner: LeagueSummoner)
    fun findById(id: LeagueSummonerPK): LeagueSummoner?
    fun findBySummonerId(summonerId: String): List<LeagueSummoner>
}
