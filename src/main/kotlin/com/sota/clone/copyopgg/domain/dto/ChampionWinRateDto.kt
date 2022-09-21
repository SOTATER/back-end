package com.sota.clone.copyopgg.domain.dto

data class ChampionWinRateDto(
    val championId: Int,
    var wins: Int,
    var losses: Int,
)