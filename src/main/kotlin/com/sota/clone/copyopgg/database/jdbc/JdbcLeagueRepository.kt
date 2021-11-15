package com.sota.clone.copyopgg.database.jdbc

import com.sota.clone.copyopgg.domain.entities.*
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class JdbcLeagueRepository(
    @Autowired val jdbc: JdbcTemplate
) : LeagueRepository {
    val logger = LoggerFactory.getLogger(LeagueRepository::class.java)

    val insertLeagueSql = "INSERT INTO leagues (league_id, tier, queue, name) " +
            "VALUES (?, ?::tier, ?::queue, ?)"
    val selectLeagueSql = "SELECT * FROM leagues WHERE league_id=?"
    val existsLeagueSql = "SELECT count(*) FROM leagues WHERE league_id=?"
    val updateLeagueByIdSql =
        "UPDATE leagues SET (tier, queue, name)=(?::tier, ?::queue, ?) where league_id=?"

    override fun save(league: League) {
        if (existsLeague(league.leagueId)) {
            updateLeague(league)
        } else {
            insertLeague(league)
        }
    }

    override fun findById(id: String): League? {
        return jdbc.queryForObject(selectLeagueSql, this::mapToLeague, id)
    }

    private fun insertLeague(league: League): Int {
        println("tier is [${league.tier}]")
        return jdbc.update(
            insertLeagueSql,
            league.leagueId,
            league.tier.toString(),
            league.queue.toString(),
            league.name
        )
    }

    private fun updateLeague(league: League) {
        jdbc.update(
            updateLeagueByIdSql,
            league.tier.toString(),
            league.queue.toString(),
            league.name,
            league.leagueId,
        )
    }

    private fun existsLeague(leagueId: String): Boolean {
        val count = jdbc.queryForObject(existsLeagueSql, Integer::class.java, leagueId)
        return count > 0
    }

    private fun mapToLeague(rs: ResultSet, rowNum: Int): League {
        return League(
            rs.getString("league_id"),
            Tier.valueOf(rs.getString("tier")),
            QueueType.valueOf(rs.getString("queue")),
            rs.getString("name")
        )
    }
}