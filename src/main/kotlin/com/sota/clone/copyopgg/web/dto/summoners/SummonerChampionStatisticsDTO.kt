package com.sota.clone.copyopgg.web.dto.summoners

import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.entities.SummonerChampionStatistics

data class SummonerChampionStatisticsDTO(
    var minionsKilledAll: Int,
    var killsAll: Int,
    var assistsAll: Int,
    var deathsAll: Int,
    var played: Int,
    var wins: Int,
    val championId: Int,
    val puuid: String,
    val season: String
) {
    fun addQueueType(summonerChampionStatistics: SummonerChampionStatistics) {
        this.minionsKilledAll += summonerChampionStatistics.minions_killed_all
        this.killsAll += summonerChampionStatistics.kills_all
        this.assistsAll += summonerChampionStatistics.assists_all
        this.deathsAll += summonerChampionStatistics.deaths_all
        this.played += summonerChampionStatistics.played
        this.wins += summonerChampionStatistics.wins
    }
}

data class SummonerChampionStatisticsQueueDTO(
    var rankedSoloFF: List<SummonerChampionStatisticsDTO>?,
    var rankedFlexSR: List<SummonerChampionStatisticsDTO>?,
    var total: List<SummonerChampionStatisticsDTO>?
)