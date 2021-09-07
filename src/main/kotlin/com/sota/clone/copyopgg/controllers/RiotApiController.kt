package com.sota.clone.copyopgg.controllers

import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Region
import com.sota.clone.copyopgg.models.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.client.RestTemplate
import java.lang.Exception

@Controller
class RiotApiController {
    val logger: Logger = LoggerFactory.getLogger(RiotApiController::class.java)
    val apiRootUrl = "https://kr.api.riotgames.com"
    val restTemplate = RestTemplate()
    val apiKey: String? = System.getenv("RIOT_API_KEY")

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
        }
    }

    fun getLeague(leagueId: String): League? {
        return try {
            apiKey?.run {
                restTemplate.getForObject(
                    "$apiRootUrl/lol/league/v4/leagues/$leagueId?api_key=$this",
                    League::class.java
                )
            }
        } catch (e: Exception) {
            logger.error("error occurred when using riot api", e)
            null
        }
    }

    fun getLeagueSummoner(summonerId: String): LeagueSummoner? {
        return try {
            apiKey?.run {
                restTemplate.getForObject(
                    "$apiRootUrl/lol/league/v4/entries/by-summoner/$summonerId?api_key=$this",
                    Array<LeagueSummoner>::class.java
                )?.run {
                    this[0]
                }
            }
        } catch (e: Exception) {
            logger.error("error occurred when using riot api", e)
            null
        }
    }
}