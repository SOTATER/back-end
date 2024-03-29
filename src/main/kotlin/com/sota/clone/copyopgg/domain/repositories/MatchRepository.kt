package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.Match
import org.springframework.data.jpa.repository.JpaRepository

interface MatchRepository {

    fun findAll(): List<Match>

    fun findLatest(): Match?

    fun findLatestMatchByPuuid(puuid: String): Match?

    fun findByGameCreationLessThanAndQueueId(gameCreation: Long, queueId: Int): List<Match>

    fun save(match: Match)

    fun findByIds(matchIds: List<String>): List<Match>
}