package com.sota.clone.copyopgg.domain.services

import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Region
import com.sota.clone.copyopgg.domain.models.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.lang.Exception

@Service
class RiotApiService {
    val logger: Logger = LoggerFactory.getLogger(RiotApiService::class.java)
    val apiRootUrl = "https://kr.api.riotgames.com"
    val restTemplate = RestTemplate()
    val apiKey: String? = System.getenv("RIOT_API_KEY")

    fun getSummoner(searchWord: String): SummonerDTO? = try {
        apiKey?.run {
            restTemplate.getForObject(
                "$apiRootUrl/lol/summoner/v4/summoners/by-name/$searchWord?api_key=$this",
                SummonerDTO::class.java
            )
        }
    } catch (e: Exception) {
        logger.error("error occurred when using riot api", e)
        null
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