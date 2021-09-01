package com.sota.clone.copyopgg.repositories

import com.sota.clone.copyopgg.models.LeagueSummoner
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

interface LeagueSummonerRepository {
    fun insertLeagueSummoners(leagueSummoners: List<LeagueSummoner>)
    fun insertLeagueSummoner(leagueSummoner: LeagueSummoner)
}

@Repository
class JdbcLeagueSummonerRepository(
    @Autowired val jdbc: JdbcTemplate
):LeagueSummonerRepository {
    val logger = LoggerFactory.getLogger(LeagueSummonerRepository::class.java)
    val insertLeagueSummonerSql =
        "INSERT INTO league_summoner (\"summoner_id\", \"league_id\", \"league_points\", \"wins\", \"loses\", \"veteran\", \"inactive\", \"fresh_blood\", \"hot_streak\")\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"

    override fun insertLeagueSummoners(leagueSummoners: List<LeagueSummoner>) {
        TODO("Not yet implemented")
    }

    override fun insertLeagueSummoner(leagueSummoner: LeagueSummoner) {
        jdbc.update(
            insertLeagueSummonerSql,
            leagueSummoner.summonerId,
            leagueSummoner.leagueId,
            leagueSummoner.leaguePoints,
            leagueSummoner.wins,
            leagueSummoner.loses,
            leagueSummoner.veteran,
            leagueSummoner.inactive,
            leagueSummoner.freshBlood,
            leagueSummoner.hotStreak
        )
    }
}