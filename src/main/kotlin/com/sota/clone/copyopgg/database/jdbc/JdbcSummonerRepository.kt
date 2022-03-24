package com.sota.clone.copyopgg.database.jdbc

import com.sota.clone.copyopgg.domain.entities.*
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class JdbcSummonerRepository(
    @Autowired val jdbc: JdbcTemplate
) : SummonerRepository {

    val logger = LoggerFactory.getLogger(SummonerRepository::class.java)

    val existsSummonerSql = "SELECT count(*) FROM summoners WHERE puuid=?"

    val selectSummonersSql =
        "SELECT * from summoners as A where lower(replace(A.name,' ','')) like lower(replace( ? , ' ','')) limit ?"

    val selectSummonerByIdSql = "SELECT * from summoners where id=?"

    val selectSummonerByNameSql =
        "select * from summoners where lower(REPLACE(\"name\", ' ',''))=lower(REPLACE(?, ' ', ''))"

    val insertSummonerSql =
        "INSERT INTO summoners (\"accountid\", \"profileiconid\", \"revisiondate\", \"name\", \"id\", \"puuid\", \"summonerlevel\")\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"

    val updateSummonerSql =
        "UPDATE summoners SET (profileiconid, revisiondate, name, summonerlevel) = (?,?,?,?) WHERE puuid=?"

    override fun save(summoner: Summoner) {
        logger.info("jdbc save called")
        if (existsSummoner(summoner.puuid)) {
            updateSummoner(summoner)
        } else {
            insertSummoner(summoner)
        }
    }

    override fun findById(id: String): Summoner? {
        logger.info("jdbc findById called")
        return jdbc.queryForObject(selectSummonerByIdSql, this::mapToSummoner, id)
    }

    override fun findSummonersByPartialName(partialName: String, size: Int): List<Summoner> {
        logger.info("jdbc findSummonersByPartialName called")
        return jdbc.query(selectSummonersSql, this::mapToSummoner, "$partialName%", size)
    }

    override fun findByName(name: String): Summoner? {
        logger.info("jdbc findByName called")
        return jdbc.queryForObject(selectSummonerByNameSql, this::mapToSummoner, name)
    }

    private fun existsSummoner(puuid: String): Boolean {
        return jdbc.queryForObject(existsSummonerSql, Integer::class.java, puuid) > 0
    }

    private fun insertSummoner(summoner: Summoner) {
        jdbc.update(
            insertSummonerSql,
            summoner.accountId,
            summoner.profileIconId,
            summoner.revisionDate,
            summoner.name,
            summoner.id,
            summoner.puuid,
            summoner.summonerLevel
        )
    }

    private fun updateSummoner(summoner: Summoner) {
        jdbc.update(
            updateSummonerSql,
            summoner.profileIconId,
            summoner.revisionDate,
            summoner.name,
            summoner.summonerLevel,
            summoner.puuid
        )
    }

    private fun mapToSummoner(rs: ResultSet, rowNum: Int): Summoner {
        return Summoner(
            accountId = rs.getString("accountid"),
            profileIconId = rs.getInt("profileiconid"),
            revisionDate = rs.getLong("revisiondate"),
            name = rs.getString("name"),
            id = rs.getString("id"),
            puuid = rs.getString("puuid"),
            summonerLevel = rs.getLong("summonerLevel")
        )
    }
}