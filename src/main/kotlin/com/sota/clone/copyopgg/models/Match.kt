package com.sota.clone.copyopgg.models

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

data class Match(
    val id: String,
    val dataVersion: String,
    val gameCreation: Long,
    val gameDuration: Long,
    val gameId: Long,
    val gameMode: GameMode,
    val gameName: String,
    val gameStartTimestamp: Long,
    val gameType: GameType,
    val gameVersion: String,
    val mapId: Int,
    val platformId: String,
    val queueId: Int,
    val tournamentCode: String
)

data class MatchSummoner(
    val matchId: String,
    val puuid: String,
    val assists: Int,
    val baronKills: Int,
    val champExperience: Int,
    val champLevel: Int,
    val championName: String,
    val championTransform: Int,
    val consumablePurchased: Int,
    val damageDealtToBuildings: Int,
    val damageDealtToObjectives: Int,
    val damageDealtToTurrets: Int,
    val damageSelfMitigated: Int,
    val deaths: Int,
    val detectorWardsPlaced: Int,
    val doubleKills: Int,
    val dragonKills: Int,
    val firstBloodAssist: Boolean,
    val firstBloodKill: Boolean,
    val firstTowerAssist: Boolean,
    val firstTowerKill: Boolean,
    val gameEndedInEarlySurrender: Boolean,
    val gameEndedInSurrender: Boolean,
    val goldEarned: Int,
    val goldSpent: Int,
    val individualPosition: IndividualPosition,
    val inhibitorKills: Int,
    val inhibitorTakedowns: Int,
    val inhibitorsLost: Int,
    val items: IntArray,
    val itemsPurchased: Int,
    val killingSprees: Int,
    val kills: Int,
    val lane: Lane,
    val largestCriticalStrike: Int,
    val largestKillingSpree: Int,
    val largestMultiKill: Int,
    val longestTimeSpentLiving: Int,
    val magicDamageDealt: Int,
    val magicDamageDealtToChampions: Int,
    val magicDamageTaken: Int,
    val neutralMinionsKilled: Int,
    val nexusKills: Int,
    val nexusLost: Int,
    val nexusTakedowns: Int,
    val objectivesStolen: Int,
    val objectivesStolenAssists: Int,
    val participantId: Int,
    val pentaKills: Int,
    val perks: Perks,
    val physicalDamageDealt: Int,
    val physicalDamageDealtToChampions: Int,
    val physicalDamageTaken: Int,
    val profileIcon: Int,
    val quadraKills: Int,
    val riotIdName: Int,
    val riotIdTagline: Int,
    val role: Role,
    val sightWardsBoughtInGame: Int,
    val spell1Casts: Int,
    val spell2Casts: Int,
    val spell3Casts: Int,
    val spell4Casts: Int,
    val summoner1Casts: Int,
    val summoner1Id: Int,
    val summoner2Casts: Int,
    val summoner2Id: Int,
    val teamEarlySurrendered: Boolean,
    val teamId: Int,
    val teamPosition: IndividualPosition,
    val timeCCingOthers: Int,
    val timePlayed: Int,
    val totalDamageDealt: Int,
    val totalDamageDealtToChampions: Int,
    val totalDamageShieldedOnTeammates: Int,
    val totalDamageTaken: Int,
    val totalHeal: Int,
    val totalHealsOnTeammates: Int,
    val totalMinionsKilled: Int,
    val totalTimeCCDealt: Int,
    val totalTimeSpentDead: Int,
    val totalUnitsHealed: Int,
    val tripleKills: Int,
    val trueDamageDealt: Int,
    val trueDamageDealtToChampions: Int,
    val trueDamageTaken: Int,
    val turretKills: Int,
    val turretTakedowns: Int,
    val turretsLost: Int,
    val unrealKills: Int,
    val visionScore: Int,
    val visionWardsBoughtInGame: Int,
    val wardsKilled: Int,
    val wardsPlaced: Int,
    val win: Boolean
)

data class MatchTeam(
    val matchId: String,
    val bans: Array<BanInfo>,
    val teamId: Int,
    val win: Boolean,
    val baronFirst: Boolean,
    val baronKills: Int,
    val championFirst: Boolean,
    val championKills: Int,
    val dragonFirst: Boolean,
    val dragonKills: Int,
    val inhibitorFirst: Boolean,
    val inhibitorKills: Int,
    val riftHeraldFirst: Boolean,
    val riftHeraldKills: Int,
    val towerFirst: Boolean,
    val towerKills: Int
)

enum class GameMode {
    ARAM, CLASSIC
}

enum class GameType {
    MATCHED_GAME
}

enum class IndividualPosition {
    TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY
}

enum class Lane {
    TOP, JUNGLE, MIDDLE, BOTTOM
}

enum class Role {
    SOLO, NONE, CARRY, SUPPORT
}

data class BanInfo(
    val championId: Int,
    val pickTurn: Int
)

data class PerkSelection(
    val perk: Int,
    val var1: Int,
    val var2: Int,
    val var3: Int
)

data class PerkStyle(
    val description: String,
    val selections: Array<PerkSelection>,
    val style: Int
)

data class StatPerks(
    val defense: Int,
    val flex: Int,
    val offense: Int
)

data class Perks(
    val statPerks: StatPerks,
    val styles: Array<PerkStyle>
)