package com.sota.clone.copyopgg.controllers

import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Region
import com.sota.clone.copyopgg.models.League
import com.sota.clone.copyopgg.models.LeagueSummoner
import com.sota.clone.copyopgg.models.SummonerDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RiotApiController {
    val logger: Logger = LoggerFactory.getLogger(RiotApiController::class.java)

    fun getSummoner(searchWord: String): SummonerDTO? {
        val fromOrianna = Orianna.summonerNamed(searchWord).withRegion(Region.KOREA).get()
        return fromOrianna.puuid?.let {
            SummonerDTO(
                accountId = fromOrianna.accountId,
                puuid = fromOrianna.puuid,
                id = fromOrianna.id,
                name = fromOrianna.name,
                summonerLevel = fromOrianna.level.toLong(),
                profileIconId = fromOrianna.profileIcon.id,
                revisionDate = fromOrianna.updated.millis
            )
        } ?: null
    }

    fun getLeague(summonerId: String): League? {
        return null
    }

    fun getLeagueSummoner(summonerId: String): LeagueSummoner? {
        return null
    }
}