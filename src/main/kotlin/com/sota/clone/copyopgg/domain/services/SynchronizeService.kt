package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.entities.League
import com.sota.clone.copyopgg.domain.entities.LeagueSummoner
import com.sota.clone.copyopgg.domain.entities.Summoner
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SynchronizeService(
    @Autowired val summonerRepository: SummonerRepository,
    @Autowired val leagueSummonerRepository: LeagueSummonerRepository,
    @Autowired val leagueRepository: LeagueRepository,
    @Autowired val riotApiService: RiotApiService
) {
    val logger: Logger = LoggerFactory.getLogger(SynchronizeService::class.java)

    fun refresh(summonerId: String) {
        logger.info("refresh called")
        // get summoner from db to get summoners puuid
        this.summonerRepository.findById(summonerId)?.run {
            val leagueSummoner = mutableListOf<LeagueSummoner>()
            val league = mutableListOf<League>()
            // call riot api to get recent summoner info by puuid
            val summoner = riotApiService.getSummonerById(this.puuid)?.let {
                Summoner(it)
            } ?: throw Exception("Summoner doesn't exist with id ${this.puuid}")
            // get all recent leagueSummoners via riot api
            leagueSummoner.addAll(riotApiService.getLeagueSummoners(summonerId).map {
                // get all recent leagues via riot api with league id of each leagueSummoner
                league.add(
                    League(
                        riotApiService.getLeague(it.leagueId)
                            ?: throw Exception("League doesn't exist with id ${it.leagueId}")
                    )
                )
                LeagueSummoner(it)
            })

            // save all recent infos
            summonerRepository.save(summoner)
            leagueSummoner.forEach { leagueSummonerRepository.save(it) }
            league.forEach { leagueRepository.save(it) }
        } ?: throw Exception("Summoner doesn't exist in DB with id $summonerId")
    }
}