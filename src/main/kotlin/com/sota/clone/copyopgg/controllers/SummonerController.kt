package com.sota.clone.copyopgg.controllers

import com.sota.clone.copyopgg.models.SummonerBriefInfo
import com.sota.clone.copyopgg.models.SummonerDTO
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
}