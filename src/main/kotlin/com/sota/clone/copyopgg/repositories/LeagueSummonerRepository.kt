package com.sota.clone.copyopgg.repositories

import com.sota.clone.copyopgg.models.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

interface LeagueSummonerRepository {
    fun insertLeagueSummoners(leagueSummoners: List<LeagueSummoner>)
    fun insertLeagueSummoner(leagueSummoner: LeagueSummoner)
    fun getLeagueSummonerBySummonerId(summonerId: String): LeagueSummoner?
}

@Repository
class JdbcLeagueSummonerRepository(
    @Autowired val jdbc: JdbcTemplate
) : LeagueSummonerRepository {
    val logger = LoggerFactory.getLogger(LeagueSummonerRepository::class.java)
    val insertLeagueSummonerSql =
        "INSERT INTO league_summoner (\"summoner_id\", \"league_id\", \"league_points\", \"rank\", \"wins\", \"loses\", \"veteran\", \"inactive\", \"fresh_blood\", \"hot_streak\")\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
    val selectLeagueSummonerSql =
        "SELECT * FROM league_summoner where \"summoner_id\"= ?"

    override fun insertLeagueSummoners(leagueSummoners: List<LeagueSummoner>) {
        TODO("Not yet implemented")
    }

    override fun insertLeagueSummoner(leagueSummoner: LeagueSummoner) {
        jdbc.update(
            insertLeagueSummonerSql,
            leagueSummoner.summonerId,
            leagueSummoner.leagueId,
            leagueSummoner.leaguePoints,
            leagueSummoner.rank,
            leagueSummoner.wins,
            leagueSummoner.loses,
            leagueSummoner.veteran,
            leagueSummoner.inactive,
            leagueSummoner.freshBlood,
            leagueSummoner.hotStreak
        )
    }

    override fun getLeagueSummonerBySummonerId(summonerId: String): LeagueSummoner? {
        return jdbc.queryForObject(selectLeagueSummonerSql, this::mapToLeagueSummoner, summonerId)
    }

    fun mapToLeagueSummoner(rs: ResultSet, rowNum: Int): LeagueSummoner {
        return LeagueSummoner(
            rs.getString("summoner_id"),
            rs.getString("league_id"),
            rs.getInt("league_points"),
            Rank.valueOf(rs.getString("rank")),
            rs.getInt("wins"),
            rs.getInt("loses"),
            rs.getBoolean("veteran"),
            rs.getBoolean("inactive"),
            rs.getBoolean("fresh_blood"),
            rs.getBoolean("hot_streak")
        )
    }
}