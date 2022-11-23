package com.sota.clone.copyopgg.domain.dto

data class MatchDto(
    val type: String,
    val averageRank: String,
    val blueTeam: MatchTeamDto,
    val redTeam: MatchTeamDto,
    val matchLengthDto: MatchLengthDto,
    val time: String,
    val winner: String
)

data class MatchTeamDto(
    val players: List<MatchPlayerDto>,
    val epicMonsterKilled: EpicMonsterKilled
)

data class EpicMonsterKilled(
    val dragon: Int,
    val baron: Int,
    val tower: Int
)

data class MatchPlayerDto(
    val name: String,
    val champion: String,
    val level: Int,
    val spells: List<String>,
    val runes: List<Int>,
    val tier: String,
    val kda: KdaDto,
    val damage: Int,
    val wardStat: WardStat,
    val cs: CreepScoreDto,
    val items: List<Int>
)

data class CreepScoreDto(
    val minion: Int,
    val monster: Int
)

data class WardStat(
    val detectorPlaced: Int,
    val set: Int,
    val unset: Int
)

data class KdaDto(
    val kills: Int,
    val deaths: Int,
    val assists: Int
)

data class MatchLengthDto(
    val minutes: Int,
    val seconds: Int
)

data class SummaryPlayerDto(
    val playerName: String,
    val playerChampion: String
)
