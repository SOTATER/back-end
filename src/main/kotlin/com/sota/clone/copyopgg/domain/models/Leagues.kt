package com.sota.clone.copyopgg.domain.models

import com.sota.clone.copyopgg.web.dto.summoners.LeagueDTO
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.*

@Entity
@Table(name = "leagues")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType::class)
class League(
    @Id
    @Column(name = "league_id")
    val leagueId: String,
    @Column(name = "tier")
    @Enumerated(javax.persistence.EnumType.STRING)
    @Type(type = "pgsql_enum")
    val tier: Tier,
    @Column(name = "queue")
    @Enumerated(javax.persistence.EnumType.STRING)
    @Type(type = "pgsql_enum")
    val queue: QueueType,
    @Column(name = "name")
    val name: String
) {
    constructor(leagueDTO: LeagueDTO) : this(
        leagueId = leagueDTO.leagueId,
        tier = leagueDTO.tier!!,
        queue = leagueDTO.queue!!,
        name = leagueDTO.name!!
    )
}