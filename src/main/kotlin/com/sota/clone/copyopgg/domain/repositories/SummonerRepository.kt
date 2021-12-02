package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.models.Summoner
import org.springframework.data.jpa.repository.JpaRepository

interface SummonerRepository {
    fun save(summoner: Summoner)
    fun findById(id: String): Summoner?
    fun findByName(name: String): Summoner?
    fun findSummonersByPartialName(partialName: String, size: Int): List<Summoner>
}

