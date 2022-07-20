package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.entities.SummonerChampionStatistics

interface SummonerChampionStatisticsRepository {
    fun save(summonerChampionStatistics: SummonerChampionStatistics)
    fun saveAll(summonerChampionStatistics: List<SummonerChampionStatistics>)
    fun findByPuuidSeason(puuid: String, season: String): List<SummonerChampionStatistics>
}