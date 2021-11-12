package com.sota.clone.copyopgg.web.rest

import com.sota.clone.copyopgg.domain.models.BooleanResponse
import com.sota.clone.copyopgg.domain.models.LeagueBriefInfoBySummoner
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import com.sota.clone.copyopgg.domain.services.RiotApiService
import com.sota.clone.copyopgg.domain.services.SummonerService
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
import io.swagger.annotations.ApiOperation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/summoners")
class SummonerController(
    @Autowired val leagueRepo: LeagueRepository,
    @Autowired val leagueSummonerRepo: LeagueSummonerRepository,
    @Autowired val riotApiController: RiotApiService,
    @Autowired val summonerService: SummonerService
) {
    val logger: Logger = LoggerFactory.getLogger(SummonerController::class.java)

    @GetMapping("/search/auto-complete/{searchWord}")
    @ApiOperation(value = "get matched names")
    fun getMatchNames(
        @PathVariable(
            name = "searchWord",
            required = true
        ) searchWord: String
    ): Iterable<Map<String, Any?>> {
        logger.info("Searching for summoner names that start with '$searchWord'")
        return this.summonerService.getFiveSummonersMatchedPartialName(searchWord).map {
            mapOf(
                "id" to it.puuid,
                "name" to it.name,
                "profileIconId" to it.profileIconId,
                "summonerLevel" to it.summonerLevel,
                "leagueInfo" to null
            )
        }
    }

    @GetMapping("/profile-info/{searchWord}")
    fun getSummonerInfo(
        @PathVariable(
            name = "searchWord",
            required = true
        ) searchWord: String
    ): ResponseEntity<Map<String, Any?>> {
        logger.info("Searching for summoner information named '$searchWord'")
        return this.summonerService.getSummonerByName(searchWord)?.let {
            ResponseEntity.ok().body(
                mapOf(
                    "id" to it.puuid,
                    "name" to it.name,
                    "profileIconId" to it.profileIconId,
                    "summonerLevel" to it.summonerLevel,
                    "leagueInfo" to null
                )
            )
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/league/brief/{searchId}")
    fun getBriefLeagueInfo(
        @PathVariable(
            name = "searchId",
            required = true
        ) searchId: String
    ): ResponseEntity<List<LeagueBriefInfoBySummoner?>> {
        logger.info("Get league brief information with summoner id: $searchId")
        return ResponseEntity.ok().body(
            this.leagueSummonerRepo.getLeagueSummonerBySummonerId(searchId).map {
                this.leagueRepo.findLeagueById(it.leagueId)?.let { league ->
                    LeagueBriefInfoBySummoner(
                        tier = league.tier,
                        wins = it.wins,
                        loses = it.losses,
                        leaguePoints = it.leaguePoints,
                        leagueName = league.name,
                        rank = it.rank,
                        queueType = league.queue.toString()
                    )
                }
            }
        )
    }

    @GetMapping("/refresh/{summonerId}")
    fun refresh(@PathVariable(name = "summonerId", required = true) summonerId: String): ResponseEntity<BooleanResponse> {
        logger.info("Synchronize data of summoner has id $summonerId")
        val result = this.riotApiController.getLeagueSummoner(summonerId)?.let { leagueSummoners ->
            leagueSummoners.forEach { leagueSummoner ->
                this.riotApiController.getLeague(leagueSummoner.leagueId)?.let { league ->
                    this.leagueSummonerRepo.syncLeagueSummoner(leagueSummoner)
                    this.leagueRepo.syncLeague(league)
                }
            }
            true
        } ?: false
        return ResponseEntity.ok().body(
            BooleanResponse(
                id = summonerId,
                result = result
            )
        )
    }
}

