package com.sota.clone.copyopgg.database.jpa

import com.sota.clone.copyopgg.domain.entities.MatchSummoner
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JpaMatchSummonerRepository : JpaRepository<MatchSummoner, Int> {

    fun findByPuuid(puuid: String, pageable: Pageable): List<MatchSummoner>

}