package com.sota.clone.copyopgg.web.rest

import com.sota.clone.copyopgg.domain.dto.ChampionWinRateDto
import com.sota.clone.copyopgg.domain.entities.Match
import com.sota.clone.copyopgg.domain.services.MatchService
import com.sota.clone.copyopgg.domain.services.RiotApiService
import com.sota.clone.copyopgg.web.dto.match.MatchSummaryDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/matches")
class MatchController(
    @Autowired
    val riotApiService: RiotApiService,
    val matchService: MatchService
) {

    @GetMapping("/summaries/by-puuid/{puuid}")
    fun getMatchSummonerSummaries(
        @PathVariable(value = "puuid") puuid: String,
        @RequestParam(value = "page", required = true) page: Int,
        @RequestParam(value = "pageSize", required = true, defaultValue = "20") pageSize: Int
    ): ResponseEntity<List<MatchSummaryDTO>> {
        return try {
            ResponseEntity.ok(matchService.getMatchSummariesByPuuid(puuid, page, pageSize))
        } catch (e: Error) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(listOf())
        }
    }

    @GetMapping("/update/by-puuid/{puuid}")
    fun updateMatchesByPuuid(@PathVariable(value = "puuid", required = true) puuid: String): ResponseEntity<Int> {
        return try {
            ResponseEntity.ok(matchService.updateMatchesByPuuid(puuid))
        } catch (e: Error) {
            ResponseEntity.badRequest().body(-1)
        }
    }

    @GetMapping("/statistics/weekly-win-rate/by-puuid/{puuid}")
    fun getWeeklyWinRate(@PathVariable(value = "puuid", required = true) puuid: String): ResponseEntity<List<ChampionWinRateDto>> {
        return try {
            ResponseEntity.ok(matchService.getWinRatiosLastSevenDays(puuid))
        } catch (e: Error) {
            ResponseEntity.notFound().build()
        }
    }
}
