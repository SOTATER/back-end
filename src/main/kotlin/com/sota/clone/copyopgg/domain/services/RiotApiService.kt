package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.entities.*
import com.sota.clone.copyopgg.web.dto.summoners.LeagueDTO
import com.sota.clone.copyopgg.web.dto.summoners.LeagueSummonerDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder
import java.lang.Exception

@Service
class RiotApiService(
    @Autowired
    private val restTemplate: RestTemplate,
    @Value(value = "\${application.riotApiKey}")
    val apiKey: String
) {
    val logger: Logger = LoggerFactory.getLogger(RiotApiService::class.java)
    val apiRootUrl = "https://kr.api.riotgames.com"
    val matchBaseUrl = "https://asia.api.riotgames.com/lol/match/v5/matches"

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


    fun getMatchTimeline() {

    }

    fun getMatchDetail() {

    }

    data class MatchIdsParams(
        val startTime: Long? = null,
        val endTime: Long? = null,
        val queue: Int? = null,
        val type: String? = null,
        val start: Int = 0,
        val count: Int = 20
    )

    fun getMatchIdsByPuuid(puuid: String, params: MatchIdsParams): List<String> {

        val headers = HttpHeaders()
        headers.set("X-Riot-Token", apiKey)

        val uriBuilder = UriComponentsBuilder.fromHttpUrl("$matchBaseUrl/by-puuid/$puuid/ids")
            .queryParam("startTime", params.startTime)
            .queryParam("endTime", params.endTime)
            .queryParam("queue", params.queue)
            .queryParam("type", params.type)
            .queryParam("start", params.start)
            .queryParam("count", params.count)

        val entity = HttpEntity<Any?>(headers)

        val response = restTemplate.exchange<List<String>>(
            uriBuilder.toUriString(),
            HttpMethod.GET,
            entity
        )

        return response.body ?: listOf()
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