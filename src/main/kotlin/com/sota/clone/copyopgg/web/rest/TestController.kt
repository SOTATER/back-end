package com.sota.clone.copyopgg.web.rest

import com.sota.clone.copyopgg.domain.dto.DataDragonChampionData
import com.sota.clone.copyopgg.domain.dto.DataDragonSummonerSpellData
import com.sota.clone.copyopgg.domain.services.DataDragonService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestController(
    private val dataDragonService: DataDragonService
) {

    @GetMapping("/spells")
    fun getSummonerSpells(
        @RequestParam("version") version: String,
        @RequestParam("language_code") languageCode: String
    ): DataDragonSummonerSpellData {
        return dataDragonService.getSummonerSpellData(version, languageCode)
    }

    @GetMapping("/champions")
    fun getChampions(
        @RequestParam("version") version: String,
        @RequestParam("language_code") languageCode: String
    ): DataDragonChampionData {
        return dataDragonService.getChampionData(version, languageCode)
    }
}