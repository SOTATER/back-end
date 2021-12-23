package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.Summoner

interface SummonerRepository {
    fun save(summoner: Summoner)
    fun findById(id: String): Summoner?
    fun findByName(name: String): Summoner?
    fun findSummonersByPartialName(partialName: String, size: Int): List<Summoner>
}

