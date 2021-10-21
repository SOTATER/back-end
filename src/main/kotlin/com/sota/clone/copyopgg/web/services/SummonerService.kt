package com.sota.clone.copyopgg.web.services

import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO

interface SummonerService {
    fun getFiveSummonersMatchedPartialName(partialName: String): List<SummonerDTO>
    fun getSummonerByName(name: String): SummonerDTO?
}