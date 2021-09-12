package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.models.SummonerBriefInfo
import com.sota.clone.copyopgg.domain.models.SummonerDTO

interface SummonerRepository {
    fun findById(id: String): SummonerDTO?
    fun searchByName(searchWord: String): SummonerBriefInfo?
    fun insertSummoner(summoner: SummonerDTO)
    fun searchFiveRowsByName(searchWord: String): Iterable<SummonerBriefInfo>
    fun insertSummoners(summoners: List<SummonerDTO>)
}
