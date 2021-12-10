package com.sota.clone.copyopgg.database.impl

import com.sota.clone.copyopgg.database.jpa.JpaMatchSummonerRepository
import com.sota.clone.copyopgg.domain.entities.MatchSummoner
import com.sota.clone.copyopgg.domain.repositories.MatchSummonerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MatchSummonerRepositoryImpl(
    @Autowired val jpaRepository: JpaMatchSummonerRepository
): MatchSummonerRepository {

    override fun saveAll(matchSummoners: List<MatchSummoner>) {
        this.jpaRepository.saveAll(matchSummoners)
    }
}