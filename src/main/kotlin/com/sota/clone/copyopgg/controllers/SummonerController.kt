package com.sota.clone.copyopgg.controllers

import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Region
import com.sota.clone.copyopgg.models.SummonerDTO
import com.sota.clone.copyopgg.models.SummonerProfileInfo
import com.sota.clone.copyopgg.repositories.JdbcSummonerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/summoners")
class SummonerController(
    @Autowired val summonerRepo: JdbcSummonerRepository
) {

    @GetMapping("/search-by-name/{searchWord}")
    fun getMatchNames(@PathVariable(name = "searchWord", required = true) searchWord: String): Iterable<SummonerDTO> {
        println("Searching for summoner names that start with $searchWord")
        return summonerRepo.searchFiveRowsByName(searchWord)
    }

    @GetMapping("/profile-info/{searchWord}")
    fun getSummonerInfo(@PathVariable(name="searchWord", required = true) searchWord: String): SummonerProfileInfo {
        println("Searching for summoner information named $searchWord")
        var result = summonerRepo.searchByName(searchWord)

        if (result == null) {
            println("Not in DB, search via riot api, key: " + System.getenv("RIOT_API_KEY"))
            val summoner = Orianna.summonerNamed(searchWord).withRegion(Region.KOREA).get()
            result = SummonerDTO(
                accountId = summoner.accountId,
                puuid = summoner.puuid,
                id = summoner.id,
                name = summoner.name,
                summonerLevel = summoner.level.toLong(),
                profileIconId = summoner.profileIcon.id,
                revisionDate = summoner.updated.millis
            )
            summonerRepo.insertSummoner(result)
        }

        return SummonerProfileInfo(
            puuid = result.puuid,
            name = result.name,
            profileIconId = result.profileIconId,
            summonerLevel = result.summonerLevel
        )
    }
}