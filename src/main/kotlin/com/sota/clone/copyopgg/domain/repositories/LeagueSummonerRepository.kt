package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.LeagueSummoner
import com.sota.clone.copyopgg.domain.entities.LeagueSummonerPK
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LeagueSummonerRepository : JpaRepository<LeagueSummoner, LeagueSummonerPK> {
    fun findBySummonerId(summonerId: String): List<LeagueSummoner>
}
