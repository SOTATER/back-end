package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.Summoner
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SummonerRepository : JpaRepository<Summoner, String> {

    fun findByName(name: String): Summoner?

    @Query("select s from Summoner s where " +
            "lower(replace(s.name, ' ', '')) like " +
            "concat('%',lower(replace(:partialName, ' ', '')),'%') " +
            "limit :size")
    fun findSummonersByPartialName(
        @Param("partialName") partialName: String,
        @Param("size") size: Int
    ): List<Summoner>
}

