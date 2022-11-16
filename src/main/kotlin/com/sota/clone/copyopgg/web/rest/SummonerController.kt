package com.sota.clone.copyopgg.web.rest

import com.sota.clone.copyopgg.domain.entities.BooleanResponse
import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.services.RiotApiService
import com.sota.clone.copyopgg.domain.services.SummonerService
import com.sota.clone.copyopgg.domain.services.SynchronizeService
import com.sota.clone.copyopgg.web.dto.summoners.QueueInfoDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerInfoDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerChampionStatisticsQueueDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerProfileDTO
import io.swagger.annotations.ApiOperation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/summoners")
class SummonerController(
    @Autowired val summonerService: SummonerService,
    @Autowired val synchronizeService: SynchronizeService
) {
    val logger: Logger = LoggerFactory.getLogger(SummonerController::class.java)

    @GetMapping("/search/auto-complete/{searchWord}")
    @ApiOperation(value = "get matched names")
    fun getMatchNames(
        @PathVariable(
            name = "searchWord",
            required = true
        ) searchWord: String
    ): ResponseEntity<List<SummonerInfoDTO>> {
        logger.info("Searching for summoner names that start with '$searchWord'")
        return try {
            ResponseEntity.ok(summonerService.getFiveSummonersMatchedPartialName(searchWord))
        } catch (e: Error) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(listOf())
        }
    }

    @GetMapping("/profile-info/{searchWord}")
    fun getSummonerInfo(
        @PathVariable(
            name = "searchWord",
            required = true
        ) searchWord: String
    ): ResponseEntity<SummonerProfileDTO> {
        logger.info("Searching for summoner information named '$searchWord'")
        return this.summonerService.getSummonerByName(searchWord)?.let {
            ResponseEntity.ok().body(SummonerProfileDTO(
                it.id,
                it.puuid,
                it.leagueInfo?.tier,
                it.leagueInfo?.rank,
                it.profileIconId,
                it.summonerLevel,
                listOf(),
                null,
                null
            ))
        } ?: ResponseEntity.ok().body(null)
    }

    @GetMapping("/league/solo/{searchId}")
    fun getSoloLeagueInfo(
        @PathVariable(
            name = "searchId",
            required = true
        ) searchId: String
    ): ResponseEntity<QueueInfoDTO> {
        logger.info("Get solo league information with summoner id: $searchId")
        return this.summonerService.getSummonerQueueInfo(searchId, QueueType.RANKED_SOLO_5x5)?.let {
            ResponseEntity.ok().body(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/league/flex/{searchId}")
    fun getFlexLeagueInfo(
        @PathVariable(
            name = "searchId",
            required = true
        ) searchId: String
    ): ResponseEntity<QueueInfoDTO> {
        logger.info("Get solo league information with summoner id: $searchId")
        return this.summonerService.getSummonerQueueInfo(searchId, QueueType.RANKED_FLEX_SR)?.let {
            ResponseEntity.ok().body(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/refresh/{summonerId}")
    fun refresh(
        @PathVariable(
            name = "summonerId",
            required = true
        ) summonerId: String
    ): ResponseEntity<BooleanResponse> {
        logger.info("Synchronize data of summoner has id $summonerId")
        return ResponseEntity.ok().body(
            BooleanResponse(
                summonerId,
                try {
                    this.synchronizeService.refresh(summonerId)
                    true
                } catch (e: Exception) {
                    logger.info(e.message)
                    false
                }
            )
        )
    }

    @GetMapping("/{puuid}/statistics/champion")
    fun getSummonerChampionStatistics(
        @PathVariable(name = "puuid",required = true) puuid: String,
        @RequestParam(value = "season", required = true) season: String
    ): ResponseEntity<SummonerChampionStatisticsQueueDTO> {
        logger.info("Get summoner champion statistics with $season")
        return ResponseEntity.ok().body(
            this.summonerService.getSummonerChampionStatistics(puuid, season)
        )
    }
}

