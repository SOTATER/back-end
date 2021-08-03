package com.sota.clone.copyopgg.controllers

import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Region
import com.merakianalytics.orianna.types.core.summoner.Summoner
import com.sota.clone.copyopgg.models.*
import com.sota.clone.copyopgg.repositories.JdbcSummonerRepository
import com.sota.clone.copyopgg.repositories.SummonerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.util.*

@RestController
@RequestMapping("/api/summoners")
class SummonerController(
    @Autowired val summonerRepo: SummonerRepository
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
        } ?: this.getSummonerViaRiotApi(searchWord)?.let {
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

    fun getSummonerViaRiotApi(searchWord: String): SummonerDTO? {
        val fromOrianna = Orianna.summonerNamed(searchWord).withRegion(Region.KOREA).get()
        return fromOrianna.puuid?.let {
            SummonerDTO(
                accountId = fromOrianna.accountId,
                puuid = fromOrianna.puuid,
                id = fromOrianna.id,
                name = fromOrianna.name,
                summonerLevel = fromOrianna.level.toLong(),
                profileIconId = fromOrianna.profileIcon.id,
                revisionDate = fromOrianna.updated.millis
            )
        } ?: null
    }

    //테스트용
    //DB에 데이터 넣는 용도
    fun loadSummonersFromRiotApi() {
        val apiKey = ""
        val template = RestTemplate()
        val response = template.getForEntity(
            "https://kr.api.riotgames.com/lol/league/v4/entries/RANKED_SOLO_5x5/DIAMOND/I?page=2&api_key=$apiKey",
            Array<SummonerLeagueDTO>::class.java
        )
        val summoners = response.body!!

        var count = 0
        var index = 0
        while (count < 30) {
            val summoner = summoners[index]
            val leagueId = summoner.leagueId
            if (!summonerRepo.existsLeagueById(leagueId)) {
                println(summoner.queueType)
                println(summoner.tier)
                val rowNum = summonerRepo.insertLeague(
                    League(
                        leagueId,
                        Tier.valueOf(summoner.tier),
                        Rank.valueOf(summoner.rank),
                        QueueType.valueOf(summoner.queueType)
                    )
                )
                if (rowNum == 1) {
                    logger.info("league $leagueId inserted to table \"leagues\"")
                }
            }
            val infoResponse = template.getForEntity(
                "https://kr.api.riotgames.com/lol/summoner/v4/summoners/${summoner.summonerId}?api_key=$apiKey",
                SummonerDTO::class.java
            )
            summonerRepo.insertSummoner(infoResponse.body!!)

            summonerRepo.insertLeagueSummoner(
                LeagueSummoner(
                    summonerId = summoner.summonerId,
                    leagueId = leagueId,
                    leaguePoints = summoner.leaguePoints,
                    wins = summoner.wins,
                    loses = summoner.losses,
                    veteran = summoner.veteran,
                    inactive = summoner.inactive,
                    freshBlood = summoner.freshBlood,
                    hotStreak = summoner.hotStreak
                )
            )
            count++
            index++
        }
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

