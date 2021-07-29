package com.sota.clone.copyopgg.controllers

import com.sota.clone.copyopgg.models.GameMode
import com.sota.clone.copyopgg.models.GameType
import com.sota.clone.copyopgg.models.Match
import com.sota.clone.copyopgg.repositories.MatchRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/matches")
class MatchController(
    @Autowired val matchRepo: MatchRepository
) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/{summonerPuuid}/update")
    fun updateAndGetRecentMatches(@PathVariable summonerPuuid: String) {
        logger.info("updating recent matches of $summonerPuuid")
    }

    private fun updateRecentMatches(puuid: String) {
        // 현재 DB에 있는 매치 중 최신의 것이 언제 것인지 확인
        
    }

}