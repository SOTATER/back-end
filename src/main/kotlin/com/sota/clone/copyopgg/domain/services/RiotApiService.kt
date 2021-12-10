package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.entities.*
import com.sota.clone.copyopgg.web.dto.summoners.LeagueDTO
import com.sota.clone.copyopgg.web.dto.summoners.LeagueSummonerDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.lang.Exception

@Service
class RiotApiService {
    val logger: Logger = LoggerFactory.getLogger(RiotApiService::class.java)
    val apiRootUrl = "https://kr.api.riotgames.com"
    val restTemplate = RestTemplate()
    val apiKey: String? = "RGAPI-fb3bf5ff-a88f-48f3-9340-d0b30c1a77b1"

//    fun getMatchHistory(puuid: String) {
//        Orianna.setRiotAPIKey(apiKey)
////        val history = Orianna.matchHistoryForSummoner(
////            Summoner
////                .withPuuid(puuid)
////                .withRegion(Region.KOREA)
////                .withPlatform(Platform.KOREA)
////                .get()
////        ).get()
//        val match = Orianna.matchWithId(5446980350).withRegion(Region.KOREA).get()
//        println(match)
//    }

    fun getSummoner(searchWord: String): SummonerDTO? = try {
        logger.info("get summoner named $searchWord via riot api")
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

    fun getSummonerById(searchId: String): SummonerDTO? = try {
        logger.info("get summoner named $searchId via riot api")
        apiKey?.run {
            restTemplate.getForObject(
                "$apiRootUrl/lol/summoner/v4/summoners/by-puuid/$searchId?api_key=$this",
                SummonerDTO::class.java
            )
        }
    } catch (e: Exception) {
        logger.error("error occurred when using riot api", e)
        null
    }

    fun getLeague(leagueId: String): LeagueDTO? {
        logger.info("get league with id $leagueId via riot api")
        return try {
            apiKey?.run {
                restTemplate.getForObject(
                    "$apiRootUrl/lol/league/v4/leagues/$leagueId?api_key=$this",
                    LeagueDTO::class.java
                )
            }
        } catch (e: Exception) {
            logger.error("error occurred when using riot api", e)
            null
        }
    }

    fun getLeagueSummoners(summonerId: String): Array<LeagueSummonerDTO> {
        logger.info("get league of summoner with id $summonerId via riot api")
        return try {
            apiKey?.run {
                restTemplate.getForObject(
                    "$apiRootUrl/lol/league/v4/entries/by-summoner/$summonerId?api_key=$this",
                    Array<LeagueSummonerDTO>::class.java
                )
            } ?: arrayOf()
        } catch (e: Exception) {
            logger.error("error occurred when using riot api", e)
            arrayOf<LeagueSummonerDTO>()
        }
    }
}