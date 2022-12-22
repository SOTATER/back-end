package com.sota.clone.copyopgg.web.rest

import com.sota.clone.copyopgg.domain.dto.MatchDto
import com.sota.clone.copyopgg.domain.dto.MatchPageDto
import com.sota.clone.copyopgg.domain.entities.BooleanResponse
import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.services.MatchService
import com.sota.clone.copyopgg.domain.services.RiotApiService
import com.sota.clone.copyopgg.domain.services.SummonerService
import com.sota.clone.copyopgg.domain.services.SynchronizeService
import com.sota.clone.copyopgg.web.constants.WebConstants
import com.sota.clone.copyopgg.web.dto.common.PageDto
import com.sota.clone.copyopgg.web.dto.summoners.QueueInfoDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerInfoDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerChampionStatisticsQueueDTO
import com.sota.clone.copyopgg.web.dto.summoners.CogameSummonerDTO
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
    @Autowired private val summonerService: SummonerService,
    @Autowired private val synchronizeService: SynchronizeService,
    @Autowired private val matchService: MatchService
) {
    val logger: Logger = LoggerFactory.getLogger(SummonerController::class.java)

    @GetMapping("/search/auto-complete/{searchWord}")
    @ApiOperation(value = "get matched names")
    fun getMatchNames(
        @PathVariable(
            name = "searchWord", required = true
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
            name = "searchWord", required = true
        ) searchWord: String
    ): ResponseEntity<SummonerInfoDTO> {
        logger.info("Searching for summoner information named '$searchWord'")
        return this.summonerService.getSummonerByName(searchWord)?.let {
            ResponseEntity.ok().body(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/league/solo/{searchId}")
    fun getSoloLeagueInfo(
        @PathVariable(
            name = "searchId", required = true
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
            name = "searchId", required = true
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
            name = "summonerId", required = true
        ) summonerId: String
    ): ResponseEntity<BooleanResponse> {
        logger.info("Synchronize data of summoner has id $summonerId")
        return ResponseEntity.ok().body(
            BooleanResponse(
                summonerId, try {
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
        @PathVariable(name = "puuid", required = true) puuid: String,
        @RequestParam(value = "season", required = true) season: String
    ): ResponseEntity<SummonerChampionStatisticsQueueDTO> {
        logger.info("Get summoner champion statistics with $season")
        return ResponseEntity.ok().body(
            this.summonerService.getSummonerChampionStatistics(puuid, season)
        )
    }

    @GetMapping("/{puuid}/matches")
    fun getMatches(
        @PathVariable(name = "puuid", required = true) puuid: String,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "${WebConstants.DEFAULT_PAGE_SIZE}") size: Int
    ): ResponseEntity<MatchPageDto> {
        return try {
            ResponseEntity.ok(matchService.getMatches(puuid, page, size))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @GetMapping("/{puuid}/cogamer")
    fun getSummonerPlayedWith(@PathVariable(value = "puuid", required = true) puuid: String): ResponseEntity<List<CogameSummonerDTO>> {
        return try {
            ResponseEntity.ok(summonerService.getSummonerPlayedWithByPuuid(puuid))
        } catch (e: Error) {
            ResponseEntity.badRequest().body(listOf())
        }
    }
}

