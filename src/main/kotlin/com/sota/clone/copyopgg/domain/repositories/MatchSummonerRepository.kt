package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.MatchSummoner
import org.springframework.data.domain.Pageable

interface MatchSummonerRepository {

    fun saveAll(matchSummoners: List<MatchSummoner>)

    fun findByPuuid(puuid: String, pageable: Pageable): List<MatchSummoner>

}