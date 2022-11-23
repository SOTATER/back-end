package com.sota.clone.copyopgg.domain.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataDragonChampionData(
    val type: String,
    val format: String,
    val version: String,
    val data: Map<String, ChampionData>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChampionData(
    val version: String,
    val id: String,
    val key: String,
    val name: String,
    val title: String,
    val blurb: String,
    val info: ChampionInfoData,
    val image: ChampionImageData,
    val tags: List<String>,
    val partype: String,
    val stats: ChampionStatsData
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChampionInfoData(
    val attack: Int,
    val defense: Int,
    val magic: Int,
    val difficulty: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChampionImageData(
    val full: String,
    val sprite: String,
    val group: String,
    val x: Int,
    val y: Int,
    val w: Int,
    val h: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChampionStatsData(
    val hp: Int,
    val hpperlevel: Int,
    val mp: Int,
    val mpperlevel: Int,
    val movespeed: Int,
    val armor: Double,
    val armorperlevel: Double,
    val spellblock: Double,
    val spellblockperlevel: Double,
    val attackrange: Int,
    val hpregen: Double,
    val hpregenperlevel: Double,
    val mpregen: Double,
    val mpregenperlevel: Double,
    val crit: Double,
    val critperlevel: Double,
    val attackdamage: Double,
    val attackdamageperlevel: Double,
    val attackspeedperlevel: Double,
    val attackspeed: Double
)