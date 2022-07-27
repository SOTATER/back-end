package com.sota.clone.copyopgg.database.jpa

import com.sota.clone.copyopgg.domain.entities.SummonerChampionStatistics
import com.sota.clone.copyopgg.domain.entities.SummonerChampionStatisticsPK
import com.sota.clone.copyopgg.domain.repositories.SummonerChampionStatisticsRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JpaSummonerChampionStatisticsRepository : JpaRepository<SummonerChampionStatistics, SummonerChampionStatisticsPK> {

    @Query("select ls from SummonerChampionStatistics ls where ls.puuid=:puuid and ls.season=:season order by ls.queue, ls.played desc")
    fun findByPuuidSeason(@Param("puuid") puuid: String, @Param("season") season: String): List<SummonerChampionStatistics>
}