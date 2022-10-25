package com.sota.clone.copyopgg.domain.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataDragonSummonerSpellData(
    val type: String,
    val version: String,
    val data: Map<String, SummonerSpellData>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SummonerSpellData(
    val id: String,
    val name: String,
    val description: String,
    val tooltip: String,
    val maxrank: Int,
    val cooldown: List<Int>,
    val cooldownBurn: String,
    val cost: List<Int>,
    val costBurn: String,
    // val datavalues: Any?,
    val effect: List<List<Int>?>,
    val effectBurn: List<String?>,
    // val vars: List<Any>,
    val key: String,
    val summonerLevel: String,
    val modes: List<String>,
    val costType: String,
    val maxammo: String,
    val range: List<Int>,
    val rangeBurn: String,
    val image: SummonerSpellImage,
    val resource: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SummonerSpellImage(
    val full: String,
    val sprite: String,
    val group: String,
    val x: Int,
    val y: Int,
    val w: Int,
    val h: Int
)