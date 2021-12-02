package com.sota.clone.copyopgg.web.dto.summoners

import com.sota.clone.copyopgg.domain.models.QueueType
import com.sota.clone.copyopgg.domain.models.Rank
import com.sota.clone.copyopgg.domain.models.Tier

data class LeagueSummonerDTO(
    val summonerId: String,
    val leagueId: String,
    val leaguePoints: Int?,
    val rank: Rank?,
    val wins: Int?,
    val losses: Int?,
    val veteran: Boolean?,
    val inactive: Boolean?,
    val freshBlood: Boolean?,
    val hotStreak: Boolean?
)

data class QueueInfoDTO(
    val summonerId: String,
    val leagueId: String,
    val tier: Tier,
    val rank: Rank,
    val leaguePoints: Int,
    val wins: Int,
    val losses: Int,
    val leagueName: String,
    val queue: QueueType
)