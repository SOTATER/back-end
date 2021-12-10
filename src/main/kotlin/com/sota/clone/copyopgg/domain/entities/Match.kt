package com.sota.clone.copyopgg.domain.entities

import java.io.Serializable
import javax.persistence.*

// data class Match
@Entity
@Table(name = "matches")
class Match(
    @Id
    @Column(name = "match_id")
    val id: String,
    @Column(name = "data_version")
    val dataVersion: String,
    @Column(name = "game_creation")
    val gameCreation: Long,
    @Column(name = "game_duration")
    val gameDuration: Long,
    @Column(name = "game_id")
    val gameId: Long,
    @Column(name = "game_mode")
    val gameMode: String,
    @Column(name = "game_name")
    val gameName: String,
    @Column(name = "game_start_timestamp")
    val gameStartTimestamp: Long,
    @Column(name = "game_type")
    val gameType: String,
    @Column(name = "game_version")
    val gameVersion: String,
    @Column(name = "map_id")
    val mapId: Int,
    @Column(name = "platform_id")
    val platformId: String,
    @Column(name = "queue_id")
    val queueId: Int,
    @Column(name = "tournament_code")
    val tournamentType: String
) {

}


data class MatchSummonersId(
    val puuid: String,
    val matchId: String
) : Serializable

@Entity
@IdClass(MatchSummonersId::class)
@Table(name = "matches_summoners")
class MatchSummoners(
    @Id
    @Column(name = "match_id")
    val matchId: String,
    @Id
    @Column(name = "puuid")
    val puuid: String,
    @Column(name= "game_ended_in_early_surrender")
    val gameEndedInEarlySurrender: Boolean,
    @Column(name = "game_ended_in_surrender")
    val gameEndedInSurrender: Boolean,
)