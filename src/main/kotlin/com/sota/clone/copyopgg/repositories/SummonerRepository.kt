package com.sota.clone.copyopgg.repositories

import com.sota.clone.copyopgg.models.SummonerDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

interface SummonerRepository {
    fun findById(id: String): SummonerDTO?
    fun searchFiveRowsByName(searchWord: String): Iterable<SummonerDTO>
}

@Repository
class JdbcSummonerRepository(
    @Autowired val jdbc: JdbcTemplate
) : SummonerRepository {

    override fun searchFiveRowsByName(searchWord: String): Iterable<SummonerDTO> {
        val result = mutableListOf<SummonerDTO>()
        return jdbc.query(
            "select * from summoners" +
                    " where lower(REPLACE(\"name\",' ','')) like lower(replace('$searchWord%', ' ', ''))" +
                    " limit 5;",
            this::mapToSummonerDTO
        )
    }

    override fun findById(id: String): SummonerDTO? {
        return jdbc.queryForObject<SummonerDTO>("select * from summoners where \"id\"=?", this::mapToSummonerDTO, id)
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