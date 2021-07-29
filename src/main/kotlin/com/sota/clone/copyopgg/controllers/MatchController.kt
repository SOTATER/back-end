package com.sota.clone.copyopgg.controllers

import com.sota.clone.copyopgg.models.GameMode
import com.sota.clone.copyopgg.models.GameType
import com.sota.clone.copyopgg.models.Match
import com.sota.clone.copyopgg.models.MatchDTO
import com.sota.clone.copyopgg.repositories.MatchRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/api/matches")
class MatchController(
    @Autowired val matchRepo: MatchRepository
) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/{summonerPuuid}/update")
    fun updateAndGetRecentMatches(@PathVariable summonerPuuid: String) {
        logger.info("updating recent matches of $summonerPuuid")
        val apiKey = "RGAPI-e091ce60-85c6-43fc-959b-9a7ebdba63b6"
        val template = RestTemplate()
        val response = template.getForEntity(
            "https://asia.api.riotgames.com/lol/match/v5/matches/KR_5355815201?&api_key=$apiKey",
            MatchDTO::class.java
        )
        logger.info("yaho!")
    }

    private fun updateRecentMatches(puuid: String) {
        // 현재 DB에 있는 매치 중 최신의 것이 언제 것인지 확인

    }

}