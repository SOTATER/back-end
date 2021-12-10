package com.sota.clone.copyopgg.database

import com.sota.clone.copyopgg.domain.entities.*
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.ResultSet

@Repository
class JdbcLeagueRepository(
    @Autowired val jdbc: JdbcTemplate
) {
    val logger = LoggerFactory.getLogger(LeagueRepository::class.java)

    val insertLeagueSql =
        "INSERT INTO leagues (\"league_id\", \"tier\", \"queue\", \"name\")\n" +
                "VALUES (?, ?::tier, ?::queue, ?)"
    val selectLeagueSql =
        "SELECT * FROM leagues WHERE \"league_id\" = ?"
    val existsLeagueSql =
        "SELECT count(*) FROM leagues WHERE \"league_id\" = ?"
    val updateLeagueByIdSql =
        "UPDATE leagues SET (tier, queue, name)=(?::tier, ?::queue, ?) where league_id=?"

    fun insertLeague(league: League): Int {
        println("tier is [${league.tier}]")
        return jdbc.update(
            insertLeagueSql,
            league.leagueId,
            league.tier.toString(),
            league.queue.toString(),
            league.name
        )
    }

    fun insertLeagues(leagues: List<League>) {
        val rows = jdbc.batchUpdate(insertLeagueSql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val league = leagues[i]
                ps.setString(1, league.leagueId)
                ps.setString(2, league.tier.toString())
                ps.setString(4, league.queue.toString())
                ps.setString(5, league.name)
            }

            override fun getBatchSize(): Int {
                return leagues.size
            }
        })
        logger.info("$rows rows are inserted into leagues table")
    }

    fun updateLeagueById(league: League) {
        jdbc.update(
            updateLeagueByIdSql,
            league.tier.toString(),
            league.queue.toString(),
            league.name,
            league.leagueId,
        )
    }

    fun syncLeague(league: League) {
        if (existsLeagueById(league.leagueId)) {
            this.updateLeagueById(league)
        } else {
            this.insertLeague(league)
        }
    }

    fun findLeagueById(leagueId: String): League? {
        return jdbc.queryForObject(selectLeagueSql, this::mapToLeague, leagueId)
    }

    fun existsLeagueById(leagueId: String): Boolean {
        val count = jdbc.queryForObject(existsLeagueSql, Integer::class.java, leagueId)
        return count > 0
    }

    fun mapToLeague(rs: ResultSet, rowNum: Int): League {
        return League(
            rs.getString("league_id"),
            Tier.valueOf(rs.getString("tier")),
            QueueType.valueOf(rs.getString("queue")),
            rs.getString("name")
        )
    }
}