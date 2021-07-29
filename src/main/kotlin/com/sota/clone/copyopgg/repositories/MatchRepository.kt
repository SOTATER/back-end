package com.sota.clone.copyopgg.repositories

import com.sota.clone.copyopgg.models.Match
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import javax.transaction.Transactional

interface MatchRepository {
    fun save(matches: List<Match>)
}

@Repository
class JdbcMatchRepository(
    @Autowired val jdbc: JdbcTemplate
) : MatchRepository {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    val insertMatchSql =
        "INSERT INTO matches VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

    @Transactional
    override fun save(matches: List<Match>) {
        val rows = jdbc.batchUpdate(insertMatchSql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val match = matches[i]

            }

            override fun getBatchSize(): Int {
                return matches.size
            }
        })
    }
}