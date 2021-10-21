package com.sota.clone.copyopgg.web.rest

import com.sota.clone.copyopgg.domain.models.BooleanResponse
import com.sota.clone.copyopgg.domain.models.LeagueBriefInfoBySummoner
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import com.sota.clone.copyopgg.domain.services.RiotApiService
import com.sota.clone.copyopgg.web.dto.summoners.SummonerInfoDTO
import com.sota.clone.copyopgg.web.services.SummonerService
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
    ): Iterable<SummonerInfoDTO> {
        logger.info("Searching for summoner names that start with '$searchWord'")
        return this.summonerService.getFiveSummonersMatchedPartialName(searchWord).map {
            SummonerInfoDTO(
                id = it.puuid,
                name = it.name,
                summonerLevel = it.summonerLevel,
                profileIconId = it.profileIconId,
                leagueInfo = null
            )
        }
    }

    @GetMapping("/profile-info/{searchWord}")
    fun getSummonerInfo(
        @PathVariable(
            name = "searchWord",
            required = true
        ) searchWord: String
    ): ResponseEntity<SummonerInfoDTO> {
        logger.info("Searching for summoner information named '$searchWord'")
        return this.summonerService.getSummonerByName(searchWord)?.let {
            ResponseEntity.ok().body(
                SummonerInfoDTO(
                    id = it.id,
                    name = it.name,
                    profileIconId = it.profileIconId,
                    summonerLevel = it.summonerLevel,
                    leagueInfo = null
                ))
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/league/brief/{searchId}")
    fun getBriefLeagueInfo(
        @PathVariable(
            name = "searchId",
            required = true
        ) searchId: String
    ): ResponseEntity<LeagueBriefInfoBySummoner> {
        logger.info("Get league brief information with summoner id: $searchId")
        return this.leagueSummonerRepo.getLeagueSummonerBySummonerId(searchId)?.let { leagueSummoner ->
            this.leagueRepo.findLeagueById(leagueSummoner.leagueId)?.let { league ->
                ResponseEntity.ok().body(
                    LeagueBriefInfoBySummoner(
                        tier = league.tier,
                        wins = leagueSummoner.wins,
                        loses = leagueSummoner.losses,
                        leaguePoints = leagueSummoner.leaguePoints,
                        leagueName = league.name,
                        rank = leagueSummoner.rank
                    )
                )
            } ?: ResponseEntity.notFound().build()
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/refresh/{summonerId}")
    fun refresh(@PathVariable(name = "summonerId", required = true) summonerId: String): ResponseEntity<BooleanResponse> {
        logger.info("Synchronize data of summoner has id $summonerId")
        val result = this.riotApiController.getLeagueSummoner(summonerId)?.let { leagueSummoner ->
            this.riotApiController.getLeague(leagueSummoner.leagueId)?.let { league ->
                this.leagueSummonerRepo.syncLeagueSummoner(leagueSummoner)
                this.leagueRepo.syncLeague(league)
                true
            } ?: false
        } ?: false
        return ResponseEntity.ok().body(
            BooleanResponse(
                id = summonerId,
                result = result
            )
        )
    }
}

data class SummonerLeagueDTO(
    val leagueId: String,
    val queueType: String,
    val tier: String,
    val rank: String,
    val summonerId: String,
    val summonerName: String,
    val leaguePoints: Int,
    val wins: Int,
    val losses: Int,
    val veteran: Boolean,
    val inactive: Boolean,
    val freshBlood: Boolean,
    val hotStreak: Boolean
)

