package com.sota.clone.copyopgg.database.impl

import com.sota.clone.copyopgg.database.jpa.JpaSummonerChampionStatisticsRepository
import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.entities.SummonerChampionStatistics
import com.sota.clone.copyopgg.domain.repositories.SummonerChampionStatisticsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class SummonerChampionStatisticsRepositoryImpl(
    @Autowired val jpaRepository: JpaSummonerChampionStatisticsRepository
) : SummonerChampionStatisticsRepository {

    override fun save(summonerChampionStatistics: SummonerChampionStatistics) {
        this.jpaRepository.save(summonerChampionStatistics)
    }

    override fun saveAll(summonerChampionStatistics: List<SummonerChampionStatistics>) {
        this.jpaRepository.saveAll(summonerChampionStatistics)
    }

    override fun findByPuuidSeason(puuid: String, season: String): List<SummonerChampionStatistics> {
        return this.jpaRepository.findByPuuidSeason(puuid, season)
    }

}