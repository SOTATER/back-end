package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.Match
import com.sota.clone.copyopgg.domain.entities.MatchSummoner
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MatchSummonerRepository {

    fun saveAll(matchSummoners: List<MatchSummoner>)

    fun findByPuuid(puuid: String, pageable: Pageable): Page<MatchSummoner>

    fun findAllSummonerPuuidsInMatches(matches: List<String>): List<String>

    fun findByMatchAndTeamId(match: Match, teamId: Int): List<MatchSummoner>

    fun findByPuuidLastGame(puuid: String): MatchSummoner?

    fun findByPuuidFirst20Games(puuid: String): List<MatchSummoner>

}