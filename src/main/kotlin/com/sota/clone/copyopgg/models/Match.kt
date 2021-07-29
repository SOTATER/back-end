package com.sota.clone.copyopgg.models

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "matches")
data class Match(
    @Id @Column(name = "match_id") val id: String,
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

//@Entity
//@Table(name = "matches_summoners")
//data class MatchSummoner(
//    @Column(name = "match_id") val matchId: String,
//    )
//
//@Entity
//@Table(name = "matches_teams")
//data class MatchTeam(
//    val matchId: String,
//
//    )

enum class GameMode {
    ARAM, CLASSIC
}

enum class GameType {
    MATCHED_GAME
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