package com.sota.clone.copyopgg.web.dto.match

data class MatchSummaryDTO(
    val champion: Int?,
    val spell1: Int?,
    val spell2: Int?,
    val kills: Int?,
    val deaths: Int?,
    val assists: Int?,
    val level: Int?,
    val cs: Int,
    val item0: Int?,
    val item1: Int?,
    val item2: Int?,
    val item3: Int?,
    val item4: Int?,
    val item5: Int?,
    val item6: Int?,
    val detectorWardsPlaced: Int?,
)