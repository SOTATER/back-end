package com.sota.clone.copyopgg.database.impl

import com.sota.clone.copyopgg.database.jpa.JpaMatchRepository
import com.sota.clone.copyopgg.domain.entities.Match
import com.sota.clone.copyopgg.domain.repositories.MatchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Component
class MatchRepositoryImpl(
    @Autowired val jpaRepository: JpaMatchRepository,
    @Autowired val jdbc: JdbcTemplate
) : MatchRepository {

    override fun findAll(): List<Match> {
        return this.jpaRepository.findAll()
    }

    override fun findLatest(): Match {
        return Match()
    }

    override fun findLatestMatchByPuuid(puuid: String): Match {
        return jpaRepository.findLatestMatchByPuuid(puuid)
    }

    override fun save(match: Match) {
        this.jpaRepository.save(match)
    }

    fun findLatestMatchInfoByPuuid(puuid: String) {
    }
}