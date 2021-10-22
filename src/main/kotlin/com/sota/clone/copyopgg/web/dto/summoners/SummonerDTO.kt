package com.sota.clone.copyopgg.web.dto.summoners

data class SummonerDTO(
    val accountId: String?,
    val profileIconId: Int?,
    val revisionDate: Long?,
    val name: String?,
    val id: String?,
    val puuid: String,
    val summonerLevel: Long?
)