package com.sota.clone.copyopgg.domain.entities

import com.sota.clone.copyopgg.web.dto.summoners.LeagueSummonerDTO
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.io.Serializable
import javax.persistence.*

@Entity
@IdClass(LeagueSummonerPK::class)
@Table(name = "league_summoner")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType::class)
class LeagueSummoner(
    @Id
    @Column(name = "summoner_id")
    val summonerId: String,
    @Id
    @Column(name = "league_id")
    val leagueId: String,
    @Column(name = "league_points")
    val leaguePoints: Int,
    @Column(name = "rank")
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    val rank: Rank,
    val wins: Int,
    val losses: Int,
    val veteran: Boolean,
    val inactive: Boolean,
    @Column(name = "fresh_blood")
    val freshBlood: Boolean,
    @Column(name = "hot_streak")
    val hotStreak: Boolean
) {
    constructor(leagueSummonerDTO: LeagueSummonerDTO) : this(
        summonerId = leagueSummonerDTO.summonerId,
        leagueId = leagueSummonerDTO.leagueId,
        leaguePoints = leagueSummonerDTO.leaguePoints!!,
        rank = leagueSummonerDTO.rank!!,
        wins = leagueSummonerDTO.wins!!,
        losses = leagueSummonerDTO.losses!!,
        veteran = leagueSummonerDTO.veteran!!,
        inactive = leagueSummonerDTO.inactive!!,
        freshBlood = leagueSummonerDTO.freshBlood!!,
        hotStreak = leagueSummonerDTO.hotStreak!!
    )
}

data class LeagueSummonerPK(
    val summonerId: String = "",
    val leagueId: String = ""
) : Serializable
