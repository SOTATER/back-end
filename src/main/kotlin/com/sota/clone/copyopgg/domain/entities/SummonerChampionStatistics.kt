package com.sota.clone.copyopgg.domain.entities

import com.sota.clone.copyopgg.web.dto.summoners.SummonerChampionStatisticsDTO
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.*
import javax.persistence.Table
import java.io.Serializable
import org.hibernate.annotations.Type


// data class for summoner champion statistics
@Entity
@IdClass(SummonerChampionStatisticsPK::class)
@Table(name="summoner_champion_statistics")
class SummonerChampionStatistics(
    @Column(name="minions_killed_all")
    var minions_killed_all: Int,
    
    @Column(name="kills_all")
    var kills_all: Int,
    
    @Column(name="assists_all")
    var assists_all: Int,
    
    @Column(name="deaths_all")
    var deaths_all: Int,
    
    @Column(name="played")
    var played: Int,
    
    @Column(name="wins")
    var wins: Int,
    
    @Id
    @Column(name="champion_id")
    val champion_id: Int,

    @Id
    @Column(name="puuid")
    val puuid: String,

    @Id
    @Column(name="season")
    val season: String,
    
    @Id
    @Column(name="queue")
    @Enumerated(javax.persistence.EnumType.STRING)
    @Type(type = "pgsql_enum")
    val queue: QueueType,
){
    fun addGame(summonerChampionStatistics: SummonerChampionStatistics) {
        this.minions_killed_all += summonerChampionStatistics.minions_killed_all
        this.kills_all += summonerChampionStatistics.kills_all
        this.assists_all += summonerChampionStatistics.assists_all
        this.deaths_all += summonerChampionStatistics.deaths_all
        this.played += 1
        this.wins += summonerChampionStatistics.wins
    }
}

data class SummonerChampionStatisticsPK(
    val champion_id: Int? = null,
    val puuid: String = "",
    val season: String = "",
    val queue: QueueType? = null
) : Serializable

