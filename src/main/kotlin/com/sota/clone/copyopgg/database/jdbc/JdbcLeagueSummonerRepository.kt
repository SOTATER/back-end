package com.sota.clone.copyopgg.database.jdbc

import com.sota.clone.copyopgg.domain.entities.*
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class JdbcLeagueSummonerRepository(
    @Autowired val jdbc: JdbcTemplate
) : LeagueSummonerRepository {
    val logger = LoggerFactory.getLogger(LeagueSummonerRepository::class.java)
    val existsLeagueSummonerBySummonerSql = "SELECT count(*) FROM league_summoner WHERE summoner_id = ?"
    val existsLeagueSummonerBySummonerAndLeagueSql =
        "SELECT count(*) FROM league_summoner WHERE summoner_id = ? AND league_id = ?"
    val selectLeagueSummonersBySummonerIdSql = "SELECT * FROM league_summoner where summoner_id=?"
    val selectLeagueSummonerSql = "SELECT * FROM league_summoner where summoner_id=? AND league_id=?"
    val insertLeagueSummonerSql =
        "INSERT INTO league_summoner (\"summoner_id\", \"league_id\", \"league_points\", \"rank\", \"wins\", \"losses\", \"veteran\", \"inactive\", \"fresh_blood\", \"hot_streak\") " +
                "VALUES (?, ?, ?, ?::rank, ?, ?, ?, ?, ?, ?)"
    val updateLeagueSummonerSql =
        "UPDATE league_summoner SET (league_points, rank, wins, losses, veteran, inactive, fresh_blood, hot_streak)\n" +
                "= (?,?::rank,?,?,?,?,?,?) where summoner_id = ? AND league_id = ?"

    override fun save(leagueSummoner: LeagueSummoner) {
        if (existsLeagueSummoner(LeagueSummonerPK(leagueSummoner.summonerId, leagueSummoner.leagueId))) {
            this.updateLeagueSummoner(leagueSummoner)
        } else {
            this.insertLeagueSummoner(leagueSummoner)
        }
    }

    override fun findById(id: LeagueSummonerPK): LeagueSummoner? {
        return jdbc.queryForObject(selectLeagueSummonerSql, this::mapToLeagueSummoner, id.summonerId, id.leagueId)
    }

    override fun findBySummonerId(summonerId: String): List<LeagueSummoner> {
        return jdbc.query(selectLeagueSummonersBySummonerIdSql, this::mapToLeagueSummoner, summonerId)
    }

    fun existsLeagueSummonerBySummoner(summonerId: String): Boolean {
        return jdbc.queryForObject(existsLeagueSummonerBySummonerSql, Integer::class.java, summonerId) > 0
    }

    private fun existsLeagueSummoner(id: LeagueSummonerPK): Boolean {
        return jdbc.queryForObject(
            existsLeagueSummonerBySummonerAndLeagueSql,
            Integer::class.java,
            id.summonerId,
            id.leagueId
        ) > 0
    }

    private fun updateLeagueSummoner(leagueSummoner: LeagueSummoner) {
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

    private fun insertLeagueSummoner(leagueSummoner: LeagueSummoner) {
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

    private fun mapToLeagueSummoner(rs: ResultSet, rowNum: Int): LeagueSummoner {
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
