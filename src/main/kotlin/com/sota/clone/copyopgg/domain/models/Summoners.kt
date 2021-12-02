package com.sota.clone.copyopgg.domain.models

import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="summoners")
class Summoner(
    @Column(name="puuid")
    val puuid: String,
    @Column(name="accountid")
    val accountId: String,
    @Column(name="profileiconid")
    val profileIconId: Int,
    @Column(name="revisiondate")
    val revisionDate: Long,
    @Column(name="name")
    val name: String,
    @Id
    @Column(name="id")
    val id: String,
    @Column(name="summonerlevel")
    val summonerLevel: Long
) {
    constructor(summonerDTO: SummonerDTO) : this(
        puuid = summonerDTO.puuid,
        accountId = summonerDTO.accountId!!,
        profileIconId = summonerDTO.profileIconId!!,
        revisionDate = summonerDTO.revisionDate!!,
        name = summonerDTO.name!!,
        id = summonerDTO.id!!,
        summonerLevel = summonerDTO.summonerLevel!!
    )
}

