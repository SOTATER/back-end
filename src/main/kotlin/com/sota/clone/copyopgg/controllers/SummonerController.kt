package com.sota.clone.copyopgg.controllers

import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Region
import com.merakianalytics.orianna.types.core.summoner.Summoner
import com.sota.clone.copyopgg.models.*
import com.sota.clone.copyopgg.repositories.JdbcSummonerRepository
import com.sota.clone.copyopgg.repositories.LeagueRepository
import com.sota.clone.copyopgg.repositories.LeagueSummonerRepository
import com.sota.clone.copyopgg.repositories.SummonerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.lang.Exception
import java.util.*

@RestController
@RequestMapping("/api/summoners")
class SummonerController(
    @Autowired val summonerRepo: SummonerRepository,
    @Autowired val leagueRepo: LeagueRepository,
    @Autowired val leagueSummonerRepo: LeagueSummonerRepository,
    @Autowired val riotApiController: RiotApiController,
) {
    val logger: Logger = LoggerFactory.getLogger(SummonerController::class.java)

    @GetMapping("/search/auto-complete/{searchWord}")
    fun getMatchNames(
        @PathVariable(
            name = "searchWord",
            required = true
        ) searchWord: String
    ): Iterable<SummonerBriefInfo> {
        logger.info("Searching for summoner names that start with '$searchWord'")
        return summonerRepo.searchFiveRowsByName(searchWord)
    }

    @GetMapping("/profile-info/{searchWord}")
    fun getSummonerInfo(
        @PathVariable(
            name = "searchWord",
            required = true
        ) searchWord: String
    ): ResponseEntity<SummonerBriefInfo> {
        logger.info("Searching for summoner information named '$searchWord'")
        val result = summonerRepo.searchByName(searchWord)
        return result?.let {
            ResponseEntity.ok().body(it)
        } ?: this.riotApiController.getSummoner(searchWord)?.let {
            summonerRepo.insertSummoner(it)
            ResponseEntity.ok().body(
                SummonerBriefInfo(
                    id = it.id,
                    name = it.name,
                    profileIconId = it.profileIconId,
                    summonerLevel = it.summonerLevel,
                    leagueInfo = null
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

