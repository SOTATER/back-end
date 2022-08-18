package com.sota.clone.copyopgg.web.dto.summoners

data class SummonerInfoDTO(
  val id: String?,
  val puuid: String?,
  val name: String?,
  val profileIconId: Int?,
  val summonerLevel: Long?,
  val leagueInfo: LeagueDTO?,
)