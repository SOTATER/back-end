package com.sota.clone.copyopgg.database.jpa

import com.sota.clone.copyopgg.domain.entities.MatchSummoner
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JpaMatchSummonerRepository : JpaRepository<MatchSummoner, Int> {

    @Query(
        value = "SELECT MS FROM MatchSummoner AS MS WHERE MS.puuid = :puuid ORDER BY MS.match.gameCreation DESC",
        countQuery = "SELECT COUNT(MS) FROM MatchSummoner AS MS WHERE MS.puuid = :puuid"
    )
    fun findByPuuid(puuid: String, pageable: Pageable): Page<MatchSummoner>

    fun findFirstByPuuidOrderByIdDesc(puuid: String): MatchSummoner?

    @Query(
        "SELECT DISTINCT MS.puuid FROM MatchSummoner AS MS WHERE MS.match.id IN :matchIds"
    )
    fun findAllSummonerPuuidsInMatches(@Param("matchIds") matchIds: List<String>): List<String>
}