package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.entities.SummonerChampionStatistics

interface SummonerChampionStatisticsRepository {
    fun save(summonerChampionStatistics: SummonerChampionStatistics)
    fun findByPuuidSeason(puuid: String, season: String): List<SummonerChampionStatistics>
    fun findById(champion_id: Int, puuid: String, season: String, queue: QueueType): SummonerChampionStatistics?
}