package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.entities.Summoner
import com.sota.clone.copyopgg.domain.entities.LeagueSummonerPK
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerChampionStatisticsRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import com.sota.clone.copyopgg.web.dto.summoners.QueueInfoDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerChampionStatisticsDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerChampionStatisticsQueueDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class SummonerService(
    @Autowired val summonerRepo: SummonerRepository,
    @Autowired val summonerChampionStatisticsRepo: SummonerChampionStatisticsRepository,
    @Autowired val leagueSummonerRepo: LeagueSummonerRepository,
    @Autowired val leagueRepository: LeagueRepository,
    @Autowired val riotApiService: RiotApiService
) {
    val logger = LoggerFactory.getLogger(SummonerService::class.java)

    fun getFiveSummonersMatchedPartialName(partialName: String): List<SummonerDTO> {
        logger.info("getFiveSummonersMatchedPartialName called")
        var list = this.summonerRepo.findSummonersByPartialName(partialName, 5)
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

    fun getSummonerByName(name: String): SummonerDTO? {
        logger.info("getSummonerByName called")
        return this.summonerRepo.findByName(name)?.let {
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
            this.summonerRepo.save(
                Summoner(
                    accountId = it.accountId!!,
                    profileIconId = it.profileIconId!!,
                    revisionDate = it.revisionDate!!,
                    name = it.name!!,
                    id = it.id!!,
                    puuid = it.puuid,
                    summonerLevel = it.summonerLevel!!
                )
            )
            it
        }
    }

    fun getSummonerQueueInfo(summonerId: String, queueType: QueueType): QueueInfoDTO? {
        logger.info("getSummonerQueueInfo called")
        var league = this.leagueSummonerRepo.findBySummonerId(summonerId).map {
            this.leagueRepository.findById(it.leagueId)?.run {
                this
            } ?: throw NoSuchElementException("There's no league with id ${it.leagueId}")
        }.filter {
            it.queue == queueType
        }

        if (league.size > 1) {
            throw Exception("Duplicate league exists")
        } else if (league.isEmpty()) {
            return null
        }

        return this.leagueSummonerRepo.findById(LeagueSummonerPK(summonerId, league[0].leagueId))?.let {
            QueueInfoDTO(
                summonerId = summonerId,
                leagueId = it.leagueId,
                tier = league[0].tier,
                rank = it.rank,
                leaguePoints = it.leaguePoints,
                wins = it.wins,
                losses = it.losses,
                leagueName = league[0].name,
                queue = queueType
            )
        }
    }

    fun getSummonerChampionStatistics(puuid: String, season: String): SummonerChampionStatisticsQueueDTO? {
        logger.info("getSummonerChampionStatistics called")
        val summonerChampionStatsitics = this.summonerChampionStatisticsRepo.findByPuuidSeason(puuid, season)

        var summonerChampionsSoloQueue = mutableListOf<SummonerChampionStatisticsDTO>()
        var summonerChampionsFlexQueue = mutableListOf<SummonerChampionStatisticsDTO>()
        var summonerChampionsTotal = mutableListOf<SummonerChampionStatisticsDTO>()

        summonerChampionStatsitics.forEach {
            if (it.queue == QueueType.RANKED_SOLO_5x5 && summonerChampionsSoloQueue.size < 7) {
                summonerChampionsSoloQueue.add(
                    SummonerChampionStatisticsDTO(
                        minionsKilledAll = it.minions_killed_all,
                        killsAll = it.kills_all,
                        assistsAll = it.assists_all,
                        deathsAll = it.deaths_all,
                        played = it.played,
                        wins = it.wins,
                        championId = it.champion_id,
                        puuid = it.puuid,
                        season = it.season
                    )
                )
            }
            else if (it.queue == QueueType.RANKED_FLEX_SR && summonerChampionsFlexQueue.size < 7) {
                summonerChampionsFlexQueue.add(
                    SummonerChampionStatisticsDTO(
                        minionsKilledAll = it.minions_killed_all,
                        killsAll = it.kills_all,
                        assistsAll = it.assists_all,
                        deathsAll = it.deaths_all,
                        played = it.played,
                        wins = it.wins,
                        championId = it.champion_id,
                        puuid = it.puuid,
                        season = it.season
                    )
                )
            }
            val index = summonerChampionsTotal.find { total -> total.championId == it.champion_id }
            if (index == null) {
                summonerChampionsTotal.add(
                    SummonerChampionStatisticsDTO(
                        minionsKilledAll = it.minions_killed_all,
                        killsAll = it.kills_all,
                        assistsAll = it.assists_all,
                        deathsAll = it.deaths_all,
                        played = it.played,
                        wins = it.wins,
                        championId = it.champion_id,
                        puuid = it.puuid,
                        season = it.season
                    )
                )
            }
            else {
                index.addQueueType(it)
            }
        }

        if (summonerChampionsTotal.size > 0) {
            summonerChampionsTotal.sortWith(compareBy({ it.played }, { it.wins }))
            summonerChampionsTotal.reverse()
            if (summonerChampionsTotal.size > 7) {
                summonerChampionsTotal = summonerChampionsTotal.subList(0,7)
            }
        }

        val summonerChampionsQueue = SummonerChampionStatisticsQueueDTO(
            summonerChampionsSoloQueue, summonerChampionsFlexQueue, summonerChampionsTotal)

        return summonerChampionsQueue
    }
}
