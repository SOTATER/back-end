package com.sota.clone.copyopgg.database.impl

import com.sota.clone.copyopgg.database.jpa.JpaMatchSummonerRepository
import com.sota.clone.copyopgg.domain.entities.Match
import com.sota.clone.copyopgg.domain.entities.MatchSummoner
import com.sota.clone.copyopgg.domain.repositories.MatchSummonerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class MatchSummonerRepositoryImpl(
    @Autowired val jpaRepository: JpaMatchSummonerRepository
) : MatchSummonerRepository {

    override fun saveAll(matchSummoners: List<MatchSummoner>) {
        this.jpaRepository.saveAll(matchSummoners)
    }

    override fun findByMatchAndTeamId(match: Match, teamId: Int): List<MatchSummoner> {
        return this.jpaRepository.findByMatchAndTeamId(match, teamId)
    }

    override fun findByPuuid(puuid: String, pageable: Pageable): Page<MatchSummoner> {
        return jpaRepository.findByPuuid(puuid, pageable)
    }

    override fun findByPuuidLastGame(puuid: String): MatchSummoner? {
        return jpaRepository.findFirstByPuuidOrderByIdDesc(puuid)
    }

    override fun findAllSummonerPuuidsInMatches(matches: List<String>): List<String> {
        return jpaRepository.findAllSummonerPuuidsInMatches(matches)
    }

    override fun findByPuuidFirst20Games(puuid: String): List<MatchSummoner> {
        return jpaRepository.findFirst20ByPuuid(puuid)
    }

}