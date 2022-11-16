package com.sota.clone.copyopgg.web.dto.summoners

import com.sota.clone.copyopgg.domain.entities.Rank
import com.sota.clone.copyopgg.domain.entities.Tier

data class SummonerProfileDTO(
    val id: String?,
    val puuid: String?,
    val currentTier: Tier?,
    val currentRank: Rank?,
    val profileIconId: Int?,
    val summonerLevel: Long?,
    val pastRank: List<SummonerPastRank>,
    val ranking: SummonerRanking?,
    val updated: Long?,
)

data class SummonerPastRank(
    val season: String?,
    val tier: Tier?,
    val rank: Rank?,
    val point: Int?,
)

data class SummonerRanking(
    val rank: Int?,
    val percentage: Double?,
)