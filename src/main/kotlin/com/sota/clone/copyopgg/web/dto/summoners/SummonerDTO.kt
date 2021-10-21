package com.sota.clone.copyopgg.web.dto.summoners

import com.sota.clone.copyopgg.domain.models.LeagueInfo

data class SummonerDTO(
    val accountId: String,
    val profileIconId: Int,
    val revisionDate: Long,
    val name: String,
    val id: String,
    val puuid: String,
    val summonerLevel: Long
)

data class SummonerInfoDTO(
    val id: String,
    val name: String,
    val summonerLevel: Long,
    val profileIconId: Int,
    val leagueInfo: LeagueInfo?,
)