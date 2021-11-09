package com.sota.clone.copyopgg.domain.models

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="summoners")
class Summoner(
    @Id
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
    @Column(name="id")
    val id: String,
    @Column(name="summonerlevel")
    val summonerLevel: Long
)

