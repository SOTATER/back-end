package com.sota.clone.copyopgg.repositories

import com.sota.clone.copyopgg.models.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.ResultSet

interface LeagueRepository {
    fun insertLeague(league: League): Int
    fun insertLeagues(leagues: List<League>)
    fun findLeagueById(leagueId: String): League?
    fun existsLeagueById(leagueId: String): Boolean
}

@Repository
class JdbcLeagueRepository(
    @Autowired val jdbc: JdbcTemplate
): LeagueRepository {
    val logger = LoggerFactory.getLogger(LeagueRepository::class.java)

    val insertLeagueSql =
        "INSERT INTO leagues (\"league_id\", \"tier\", \"rank\", \"queue\")\n" +
                "VALUES (?, ?::tier, ?::rank, ?::queue)"

    val selectLeagueSql =
        "SELECT * FROM leagues WHERE \"league_id\" = ?"

    val existsLeagueSql =
        "SELECT count(*) FROM leagues WHERE \"league_id\" = ?"

    override fun insertLeague(league: League): Int {
        println("tier is [${league.tier}]")
        return jdbc.update(
            insertLeagueSql,
            league.leagueId,
            league.tier.toString(),
            league.rank.toString(),
            league.queue.toString()
        )
    }

    override fun insertLeagues(leagues: List<League>) {
        val rows = jdbc.batchUpdate(insertLeagueSql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val league = leagues[i]
                ps.setString(1, league.leagueId)
                ps.setString(2, league.tier.toString())
                ps.setString(3, league.rank.toString())
                ps.setString(4, league.queue.toString())
            }

            override fun getBatchSize(): Int {
                return leagues.size
            }
        })
        logger.info("$rows rows are inserted into leagues table")
    }

    override fun findLeagueById(leagueId: String): League? {
        return jdbc.queryForObject(selectLeagueSql, this::mapToLeague, leagueId)
    }

    override fun existsLeagueById(leagueId: String): Boolean {
        val count = jdbc.queryForObject(existsLeagueSql, Integer::class.java, leagueId)
        return count > 0
    }

    fun mapToLeague(rs: ResultSet, rowNum: Int): League {
        return League(
            rs.getString("league_id"),
            Tier.valueOf(rs.getString("tier")),
            Rank.valueOf(rs.getString("rank")),
            QueueType.valueOf(rs.getString("queue"))
        )
    }
}