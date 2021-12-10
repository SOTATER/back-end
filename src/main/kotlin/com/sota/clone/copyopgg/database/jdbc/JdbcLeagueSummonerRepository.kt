package com.sota.clone.copyopgg.database

import com.sota.clone.copyopgg.domain.entities.*
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class JdbcLeagueSummonerRepository(
    @Autowired val jdbc: JdbcTemplate
) {
    val logger = LoggerFactory.getLogger(LeagueSummonerRepository::class.java)
    val existsLeagueSummonerBySummonerSql =
        "SELECT count(*) FROM league_summoner WHERE summoner_id = ?"
    val existsLeagueSummonerBySummonerAndLeagueSql =
        "SELECT count(*) FROM league_summoner WHERE summoner_id = ? AND league_id = ?"
    val insertLeagueSummonerSql =
        "INSERT INTO league_summoner (\"summoner_id\", \"league_id\", \"league_points\", \"rank\", \"wins\", \"losses\", \"veteran\", \"inactive\", \"fresh_blood\", \"hot_streak\")\n" +
                "VALUES (?, ?, ?, ?::rank, ?, ?, ?, ?, ?, ?)"
    val selectLeagueSummonerSql =
        "SELECT * FROM league_summoner where \"summoner_id\"= ?"
    val updateLeagueSummonerSql =
        "UPDATE league_summoner SET (league_points, rank, wins, losses, veteran, inactive, fresh_blood, hot_streak)\n" +
                "= (?,?::rank,?,?,?,?,?,?) where summoner_id = ? AND league_id = ?"

    fun existsLeagueSummonerBySummoner(summonerId: String): Boolean {
        return jdbc.queryForObject(existsLeagueSummonerBySummonerSql, Integer::class.java, summonerId) > 0
    }

    fun existsLeagueSummonerBySummonerAndLeague(summonerId: String, leagueId: String): Boolean {
        return jdbc.queryForObject(existsLeagueSummonerBySummonerAndLeagueSql, Integer::class.java, summonerId, leagueId) > 0
    }

    fun insertLeagueSummoners(leagueSummoners: List<LeagueSummoner>) {
        TODO("Not yet implemented")
    }

    fun updateLeagueSummonerBySummoner(leagueSummoner: LeagueSummoner) {
        jdbc.update(
            updateLeagueSummonerSql,
            leagueSummoner.leaguePoints,
            leagueSummoner.rank.toString(),
            leagueSummoner.wins,
            leagueSummoner.losses,
            leagueSummoner.veteran,
            leagueSummoner.inactive,
            leagueSummoner.freshBlood,
            leagueSummoner.hotStreak,
            leagueSummoner.summonerId,
            leagueSummoner.leagueId,
        )
    }

    fun syncLeagueSummoner(leagueSummoner: LeagueSummoner) {
        if (this.existsLeagueSummonerBySummonerAndLeague(leagueSummoner.summonerId, leagueSummoner.leagueId)) {
            updateLeagueSummonerBySummoner(leagueSummoner)
        } else {
            insertLeagueSummoner(leagueSummoner)
        }
    }

    fun insertLeagueSummoner(leagueSummoner: LeagueSummoner) {
        jdbc.update(
            insertLeagueSummonerSql,
            leagueSummoner.summonerId,
            leagueSummoner.leagueId,
            leagueSummoner.leaguePoints,
            leagueSummoner.rank.toString(),
            leagueSummoner.wins,
            leagueSummoner.losses,
            leagueSummoner.veteran,
            leagueSummoner.inactive,
            leagueSummoner.freshBlood,
            leagueSummoner.hotStreak
        )
    }

    fun getLeagueSummonerBySummonerId(summonerId: String): Array<LeagueSummoner> {
        return if (this.existsLeagueSummonerBySummoner(summonerId)) {
            jdbc.query(selectLeagueSummonerSql, this::mapToLeagueSummoner, summonerId).toTypedArray()
        } else {
            arrayOf<LeagueSummoner>()
        }
    }

    fun mapToLeagueSummoner(rs: ResultSet, rowNum: Int): LeagueSummoner {
        return LeagueSummoner(
            rs.getString("summoner_id"),
            rs.getString("league_id"),
            rs.getInt("league_points"),
            Rank.valueOf(rs.getString("rank")),
            rs.getInt("wins"),
            rs.getInt("losses"),
            rs.getBoolean("veteran"),
            rs.getBoolean("inactive"),
            rs.getBoolean("fresh_blood"),
            rs.getBoolean("hot_streak")
        )
    }
}
