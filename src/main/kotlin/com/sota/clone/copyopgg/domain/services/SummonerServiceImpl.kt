package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.models.Summoner
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
import com.sota.clone.copyopgg.web.services.SummonerService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SummonerServiceImpl(
    @Autowired val repo: SummonerRepository,
    @Autowired val riotApiService: RiotApiService
): SummonerService {
    val logger = LoggerFactory.getLogger(SummonerServiceImpl::class.java)

    override fun getFiveSummonersMatchedPartialName(partialName: String): List<SummonerDTO> {
        logger.info("getFiveSummonersMatchedPartialName called")
        var list = this.repo.findSummonersByPartialName(partialName)
        if(list.size > 5) {
            list = list.subList(0, 5)
        }
        return list.map {
            SummonerDTO(
                accountId = it.accountId,
                profileIconId = it.profileIconId,
                revisionDate = it.revisionDate,
                name = it.name,
                id = it.id,
                puuid = it.puuid,
                summonerLevel = it.summonerLevel
            )
        }
    }

    override fun getSummonerByName(name: String): SummonerDTO? {
        logger.info("getSummonerByName called")
        return this.repo.findByName(name)?.let {
            SummonerDTO(
                accountId = it.accountId,
                profileIconId = it.profileIconId,
                revisionDate = it.revisionDate,
                name = it.name,
                id = it.id,
                puuid = it.puuid,
                summonerLevel = it.summonerLevel
            )
        } ?: riotApiService.getSummoner(name)?.let {
            this.repo.save(Summoner(
                accountId = it.accountId,
                profileIconId = it.profileIconId,
                revisionDate = it.revisionDate,
                name = it.name,
                id = it.id,
                puuid = it.puuid,
                summonerLevel = it.summonerLevel
            ))
            it
        }
    }
}