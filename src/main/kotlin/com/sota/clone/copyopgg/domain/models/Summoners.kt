package com.sota.clone.copyopgg.domain.models

// data class we can get from riot summoner api
data class SummonerDTO(
    val accountId: String,
    val profileIconId: Int,
    val revisionDate: Long,
    val name: String,
    val id: String,
    val puuid: String,
    val summonerLevel: Long
)

// data class for search-by-name api response
data class SummonerBriefInfo(
    val id: String,
    val name: String,
    val summonerLevel: Long,
    val profileIconId: Int,
    val leagueInfo: LeagueInfo?,
)

