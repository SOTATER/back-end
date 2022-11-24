package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.entities.Summoner
import com.sota.clone.copyopgg.domain.entities.LeagueSummonerPK
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.MatchSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerChampionStatisticsRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import com.sota.clone.copyopgg.web.dto.summoners.CogameSummonerDTO
import com.sota.clone.copyopgg.web.dto.summoners.QueueInfoDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerInfoDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerChampionStatisticsDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerChampionStatisticsQueueDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception

@Service
class SummonerService(
    @Autowired val matchSummonerRepo: MatchSummonerRepository,
    @Autowired val summonerRepo: SummonerRepository,
    @Autowired val summonerChampionStatisticsRepo: SummonerChampionStatisticsRepository,
    @Autowired val leagueSummonerRepo: LeagueSummonerRepository,
    @Autowired val leagueRepository: LeagueRepository,
    @Autowired val riotApiService: RiotApiService
) {
    val logger = LoggerFactory.getLogger(SummonerService::class.java)

    fun getFiveSummonersMatchedPartialName(partialName: String): List<SummonerInfoDTO> {
        logger.info("getFiveSummonersMatchedPartialName called")
        var list = this.summonerRepo.findSummonersByPartialName(partialName, 5)
        return list.map {
            SummonerInfoDTO(
                id = it.id,
                puuid = it.puuid,
                name = it.name,
                profileIconId = it.profileIconId,
                summonerLevel = it.summonerLevel,
                leagueInfo = null,
            )
        }
    }

    fun getSummonersByPuuids(puuids: List<String>): List<SummonerDTO> {
        // 이미 DB에 존재하는 소환사들
        val existing = summonerRepo.findByPuuids(puuids).associateBy { it.puuid }
        // DB에 존재하지 않는 소환사들 puuid 목록
        val none = puuids.filter { !existing.containsKey(it) }
        val newSummoners = none.mapNotNull { riotApiService.getSummonerByPuuid(it) }
        summonerRepo.saveAll(newSummoners.map { Summoner(it) })
        return listOf(*existing.values.map { SummonerDTO.fromEntity(it) }.toTypedArray(), *newSummoners.toTypedArray())
    }

    fun getSummonerByPuuid(puuid: String): SummonerDTO {
        val summoner = summonerRepo.findByPuuid(puuid)
        return if (summoner != null) {
            SummonerDTO.fromEntity(summoner)
        } else {
            val fetched = riotApiService.getSummonerByPuuid(puuid)
            if (fetched == null) {
                throw Exception("puuid does not exist.")
            } else {
                summonerRepo.save(Summoner(fetched))
                fetched
            }
        }
    }

    fun getSummonerByName(name: String): SummonerInfoDTO? {
        logger.info("getSummonerByName called")
        return this.summonerRepo.findByName(name)?.let {
            SummonerInfoDTO(
                id = it.id,
                puuid = it.puuid,
                name = it.name,
                profileIconId = it.profileIconId,
                summonerLevel = it.summonerLevel,
                leagueInfo = null,
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
            SummonerInfoDTO(
                id = it.id,
                puuid = it.puuid,
                name = it.name,
                profileIconId = it.profileIconId,
                summonerLevel = it.summonerLevel,
                leagueInfo = null,
            )
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

        val summonerChampionsSoloQueue = mutableListOf<SummonerChampionStatisticsDTO>()
        val summonerChampionsFlexQueue = mutableListOf<SummonerChampionStatisticsDTO>()
        val summonerChampionsTotalMap = mutableMapOf<Int, SummonerChampionStatisticsDTO>()

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
            } else if (it.queue == QueueType.RANKED_FLEX_SR && summonerChampionsFlexQueue.size < 7) {
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
            summonerChampionsTotalMap[it.champion_id]?.addQueueType(it) ?: run {
                summonerChampionsTotalMap[it.champion_id] = SummonerChampionStatisticsDTO(
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
            }
        }

        var summonerChampionsTotalList = summonerChampionsTotalMap.values.toMutableList()
        if (summonerChampionsTotalList.size > 0) {
            summonerChampionsTotalList.sortWith(compareBy({ it.played }, { it.wins }))
            summonerChampionsTotalList.reverse()
            if (summonerChampionsTotalList.size > 7) {
                summonerChampionsTotalList = summonerChampionsTotalList.subList(0, 7)
            }
        }

        val summonerChampionsQueue = SummonerChampionStatisticsQueueDTO(
            summonerChampionsSoloQueue, summonerChampionsFlexQueue, summonerChampionsTotalList
        )

        return summonerChampionsQueue
    }

    fun getSummonerPlayedWithByPuuid(puuid: String): List<CogameSummonerDTO> {
        val playedWithCounter = mutableMapOf<String,PlayedWithInfo>()

        matchSummonerRepo.findByPuuidFirst20Games(puuid).forEach { matchSummoner ->
            matchSummoner.match!!.matchTeams.filter { it.teamId == matchSummoner.teamId }.single().win!!.also { won ->
                matchSummonerRepo.findByMatchAndTeamId(matchSummoner.match!!, matchSummoner.teamId!!).filter { it.puuid != puuid }.forEach { matchSummonerPlayedWith ->
                    val summonerPlayedWith = this.summonerRepo.findByPuuid(matchSummonerPlayedWith.puuid!!)?: run {
                        val summonerDto = riotApiService.getSummonerById(matchSummonerPlayedWith.puuid!!)
                        Summoner(
                            accountId = summonerDto!!.accountId!!,
                            profileIconId = summonerDto.profileIconId!!,
                            revisionDate = summonerDto.revisionDate!!,
                            name = summonerDto.name!!,
                            id = summonerDto.id!!,
                            puuid = summonerDto.puuid,
                            summonerLevel = summonerDto.summonerLevel!!
                        ).also { summoner ->
                            this.summonerRepo.save(summoner)
                        }
                    }
                    playedWithCounter[summonerPlayedWith.name]?.let { playedWithInfo ->
                        playedWithInfo.games += 1
                        if (won) {
                            playedWithInfo.win += 1
                        }
                        else {
                            playedWithInfo.lose += 1
                        }
                    } ?: run {
                        if (won) {
                            playedWithCounter[summonerPlayedWith.name] = PlayedWithInfo(1,1,0)
                        }
                        else {
                            playedWithCounter[summonerPlayedWith.name] = PlayedWithInfo(1,0,1)
                        }
                    }
                }
            }
        }

        return playedWithCounter.toList().sortedByDescending { it.second.games }.asIterable().take(10).map {
            CogameSummonerDTO(
                name = it.first,
                games = it.second.games,
                win = it.second.win,
                lose = it.second.lose
            )
        }
    }

}

data class PlayedWithInfo(
    var games: Int = 0,
    var win: Int = 0,
    var lose: Int = 0
)