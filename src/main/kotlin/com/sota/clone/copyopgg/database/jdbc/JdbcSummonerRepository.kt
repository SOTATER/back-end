package com.sota.clone.copyopgg.database

import com.sota.clone.copyopgg.domain.models.*
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import com.sota.clone.copyopgg.web.dto.summoners.LeagueDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class JdbcSummonerRepository(
    @Autowired val jdbc: JdbcTemplate
) {

    val logger = LoggerFactory.getLogger(SummonerRepository::class.java)

    val selectFiveSummonerBriefInfoSql =
        "select A.id, A.name, A.summonerlevel, A.profileiconid, B.league_id, B.tier, B.rank, B.league_points\n" +
                "from summoners A\n" +
                "left join (\n" +
                "  select LS.summoner_id, LS.league_id, LS.league_points, L.tier, L.rank \n" +
                "  from league_summoner LS\n" +
                "  inner join leagues L\n" +
                "  on LS.league_id = L.league_id\n" +
                ") as B\n" +
                "on A.id = B.summoner_id\n" +
                "where lower(replace(A.name,' ','')) like lower(replace( ? , ' ','')) limit 5;"

    val selectSummonerByNameSql =
        "select id, name, summonerlevel, profileiconid from summoners where lower(REPLACE(\"name\", ' ',''))=lower(REPLACE(?, ' ', ''))"

    val insertSummonerSql =
        "INSERT INTO summoners (\"accountid\", \"profileiconid\", \"revisiondate\", \"name\", \"id\", \"puuid\", \"summonerlevel\")\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"

    val findSummonerByIdSql = "select * from summoners where \"id\"=?"

    fun insertSummoner(summoner: SummonerDTO) {
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

    fun searchFiveRowsByName(searchWord: String): Iterable<SummonerDTO> {
        return jdbc.query(
            this.selectFiveSummonerBriefInfoSql,
            this::mapToSummonerBriefInfo,
            "$searchWord%"
        )
    }

    fun searchByName(searchWord: String): SummonerDTO? {
        val result = jdbc.query(selectSummonerByNameSql, this::mapToSummonerBriefInfo, "$searchWord")

        return if (result.size == 1) result[0] else null
    }

    fun findById(id: String): SummonerDTO? {
        return jdbc.queryForObject<SummonerDTO>(findSummonerByIdSql, this::mapToSummonerDTO, id)
    }

    fun mapToSummonerBriefInfo(rs: ResultSet, rowNum: Int): SummonerDTO {
        val leagueInfo = if (RepositoryUtils.columnExistsInResultSet(rs, "league_id")) {
            val leagueId = rs.getString("league_id")
            if (leagueId == null) null
            else LeagueDTO(
                leagueId = leagueId,
                tier = Tier.valueOf(rs.getString("tier")),
                rank = Rank.valueOf(rs.getString("rank")),
                leaguePoints = rs.getInt("league_points"),
                name = null,
                queue = null
            )
        } else null

        return SummonerDTO(
            puuid = rs.getString("id"),
            name = rs.getString("name"),
            summonerLevel = rs.getLong("summonerlevel"),
            profileIconId = rs.getInt("profileiconid"),
            revisionDate = null,
            accountId = null,
            id = null
        )
    }

    fun mapToSummonerDTO(rs: ResultSet, rowNum: Int): SummonerDTO {
        return SummonerDTO(
            rs.getString("accountid"),
            rs.getInt("profileiconid"),
            rs.getLong("revisiondate"),
            rs.getString("name"),
            rs.getString("id"),
            rs.getString("puuid"),
            rs.getLong("summonerLevel")
        )
    }
}