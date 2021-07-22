package com.sota.clone.copyopgg.repositories

import com.sota.clone.copyopgg.models.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.ResultSet

interface SummonerRepository {
    fun findById(id: String): SummonerDTO?
    fun searchByName(searchWord: String): SummonerDTO?
    fun insertSummoner(summoner: SummonerDTO)
    fun searchFiveRowsByName(searchWord: String): Iterable<SummonerBriefInfo>
    fun insertSummoners(summoners: List<SummonerDTO>)
    fun insertLeague(league: League): Int
    fun insertLeagueSummoner(leagueSummoner: LeagueSummoner)
    fun insertLeagues(leagues: List<League>)
    fun insertLeagueSummoners(leagueSummoners: List<LeagueSummoner>)
    fun findLeagueById(leagueId: String): League?
    fun existsLeagueById(leagueId: String): Boolean
}

@Repository
class JdbcSummonerRepository(
    @Autowired val jdbc: JdbcTemplate
) : SummonerRepository {

    val logger = LoggerFactory.getLogger(SummonerRepository::class.java)

    val insertLeagueSummonerSql =
        "INSERT INTO league_summoner (\"summoner_id\", \"league_id\", \"league_points\", \"wins\", \"loses\", \"veteran\", \"inactive\", \"fresh_blood\", \"hot_streak\")\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"

    val insertLeagueSql =
        "INSERT INTO leagues (\"league_id\", \"tier\", \"rank\", \"queue\")\n" +
                "VALUES (?, ?::tier, ?::rank, ?::queue)"

    val selectLeagueSql =
        "SELECT * FROM leagues WHERE \"league_id\" = ?"

    val existsLeagueSql =
        "SELECT count(*) FROM leagues WHERE \"league_id\" = ?"

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

    val insertSummonerSql =
        "INSERT INTO summoners (\"accountid\", \"profileiconid\", \"revisiondate\", \"name\", \"id\", \"puuid\", \"summonerlevel\")\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"

    val findSummonerByIdSql = "select * from summoners where \"id\"=?"

    override fun insertSummoners(summoners: List<SummonerDTO>) {

    }

    override fun insertSummoner(summoner: SummonerDTO) {
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

    override fun insertLeagueSummoners(leagueSummoners: List<LeagueSummoner>) {
        TODO("Not yet implemented")
    }

    override fun findLeagueById(leagueId: String): League? {
        return jdbc.queryForObject(selectLeagueSql, this::mapToLeague, leagueId)
    }

    override fun existsLeagueById(leagueId: String): Boolean {
        val count = jdbc.queryForObject(existsLeagueSql, Integer::class.java, leagueId)
        return count > 0
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

    override fun searchFiveRowsByName(searchWord: String): Iterable<SummonerBriefInfo> {
        val result = mutableListOf<SummonerBriefInfo>()
        return jdbc.query(
            this.selectFiveSummonerBriefInfoSql,
            this::mapToSummonerBriefInfo,
            "$searchWord%"
        )
    }

    override fun searchByName(searchWord: String): SummonerDTO? {
        val request = "select * from summoners" +
                " where lower(REPLACE(\"name\", ' ',''))=lower(REPLACE('$searchWord', ' ', ''))"
        println(request)
        val result = jdbc.query(request, this::mapToSummonerDTO)
        println(result)

        return if (result.size == 1) result[0] else null
    }

    override fun findById(id: String): SummonerDTO? {
        return jdbc.queryForObject<SummonerDTO>(findSummonerByIdSql, this::mapToSummonerDTO, id)
    }

    fun mapToLeague(rs: ResultSet, rowNum: Int): League {
        return League(
            rs.getString("league_id"),
            Tier.valueOf(rs.getString("tier")),
            Rank.valueOf(rs.getString("rank")),
            QueueType.valueOf(rs.getString("queue"))
        )
    }

    fun mapToSummonerBriefInfo(rs: ResultSet, rowNum: Int): SummonerBriefInfo {

        val leagueId = rs.getString("league_id")
        val tier = rs.getString("tier")
        val rank = rs.getString("rank")
        val leaguePoints = rs.getInt("league_points")

        val leagueInfo = if (leagueId != null) {
            LeagueInfo(
                leagueId,
                Tier.valueOf(tier),
                Rank.valueOf(rank),
                leaguePoints
            )
        } else null

        return SummonerBriefInfo(
            rs.getString("id"),
            rs.getString("name"),
            rs.getLong("summonerlevel"),
            rs.getInt("profileiconid"),
            leagueInfo
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