package com.sota.clone.copyopgg.web.dto.summoners

import com.sota.clone.copyopgg.domain.entities.Summoner

data class SummonerDTO(
    val accountId: String?,
    val profileIconId: Int?,
    val revisionDate: Long?,
    val name: String?,
    val id: String?,
    val puuid: String,
    val summonerLevel: Long?
) {

    companion object {
        fun fromEntity(entity: Summoner): SummonerDTO {
            return SummonerDTO(
                accountId = entity.accountId,
                profileIconId =  entity.profileIconId,
                revisionDate = entity.revisionDate,
                name = entity.name,
                id = entity.id,
                puuid = entity.puuid,
                summonerLevel = entity.summonerLevel
            )
        }
    }
}