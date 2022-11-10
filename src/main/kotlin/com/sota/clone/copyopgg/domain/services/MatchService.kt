package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.dto.ChampionWinRateDto
import com.sota.clone.copyopgg.domain.dto.MatchDto
import com.sota.clone.copyopgg.domain.entities.GameType
import com.sota.clone.copyopgg.domain.entities.Match
import com.sota.clone.copyopgg.domain.entities.SummonerChampionStatistics
import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.repositories.MatchRepository
import com.sota.clone.copyopgg.domain.repositories.MatchSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.MatchTeamRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerChampionStatisticsRepository
import com.sota.clone.copyopgg.web.dto.match.MatchSummaryDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.lang.Exception
import java.util.*

@Service
class MatchService(
    @Autowired val matchRepo: MatchRepository,
    @Autowired val matchSummonerRepo: MatchSummonerRepository,
    @Autowired val matchTeamRepo: MatchTeamRepository,
    @Autowired val summonerChampionStatisticsRepo: SummonerChampionStatisticsRepository,
    @Autowired val riotApiService: RiotApiService,
    val logger: Logger = LoggerFactory.getLogger(MatchService::class.java)
) {

    fun getMatchSummariesByPuuid(puuid: String, page: Int, pageSize: Int): List<MatchSummaryDTO> {
        val pageable: Pageable = PageRequest.of(page, pageSize)
        val matchSummoners = matchSummonerRepo.findByPuuid(puuid, pageable)

        return matchSummoners.map { matchSummoner ->
            MatchSummaryDTO(
                champion = matchSummoner.matchSummonerChampion!!.championId,
                spell1 = matchSummoner.matchSummonerChampion!!.summoner1Id,
                spell2 = matchSummoner.matchSummonerChampion!!.summoner2Id,
                kills = matchSummoner.matchSummonerCombat!!.kills,
                deaths = matchSummoner.matchSummonerCombat!!.deaths,
                assists = matchSummoner.matchSummonerCombat!!.assists,
                level = matchSummoner.matchSummonerChampion!!.champLevel,
                cs = matchSummoner.matchSummonerObjective!!.totalMinionsKilled!! + matchSummoner.matchSummonerObjective!!.neutralMinionsKilled!!,
                item0 = matchSummoner.matchSummonerItem!!.item0,
                item1 = matchSummoner.matchSummonerItem!!.item1,
                item2 = matchSummoner.matchSummonerItem!!.item2,
                item3 = matchSummoner.matchSummonerItem!!.item3,
                item4 = matchSummoner.matchSummonerItem!!.item4,
                item5 = matchSummoner.matchSummonerItem!!.item5,
                item6 = matchSummoner.matchSummonerItem!!.item6,
                detectorWardsPlaced = matchSummoner.matchSummonerVision!!.detectorWardsPlaced,
            )
        }
    }

    fun updateMatchesByPuuid(puuid: String): Int {
        // 현재 저장된 플레이어의 매치 중 가장 최신의 것의 id를 가져온다.
        logger.info("updateMatchesByPuuid called")
        val latestMatch = matchSummonerRepo.findByPuuidLastGame(puuid)
        // riot api를 통해 가져올 매치들의 id 목록 가져오기
        val ids = latestMatch?.let {
            riotApiService.getMatchIdsByPuuid(
                puuid,
                RiotApiService.MatchIdsParams(
                    startTime = it.match?.gameStartTimestamp!! / 1000
                )
                ).reversed()
                .filter { matchId -> matchId != it.match?.id }
        } ?: riotApiService.getMatchIdsByPuuid(puuid, null)

        // 매치 id들을 이용하여 챔피언 통계 update
        updateSummonerChampionStatistics(ids, puuid)

        // Riot API를 통해 해당 매치 이후의 매치들을 가져온다
        ids.forEach { matchId -> migrateMatchData(matchId) }
        return ids.size - 1
    }

    private fun migrateMatchData(matchId: String) {
        // get match data from riot api
        val matchDto = riotApiService.getMatchDetail(matchId)

        matchDto?.toMatch()?.let { match ->
            matchRepo.save(match)
            matchSummonerRepo.saveAll(match.matchSummoners)
            matchTeamRepo.saveAll(match.matchTeams)
        }
    }

    private fun updateSummonerChampionStatistics(ids: List<String>, puuid: String) {
        //ids 읽으면서 summonerChampionrepo에 save
        val mySummonerChampionStatisticsList = mutableListOf<SummonerChampionStatistics>()
        ids.forEach { matchId ->
            val detail = riotApiService.getMatchDetail(matchId) ?: return@forEach
            val myDetail = detail.info.participants.find({ i -> i.puuid == puuid }) ?: return@forEach
            if (detail.info.queueId != 420 && detail.info.queueId != 440) {
                return@forEach
            }
            mySummonerChampionStatisticsList.add(
                SummonerChampionStatistics(
                    minions_killed_all = myDetail.totalMinionsKilled,
                    kills_all = myDetail.kills,
                    assists_all = myDetail.assists,
                    deaths_all = myDetail.deaths,
                    played = 1,
                    wins = if (myDetail.win) 1 else 0,
                    champion_id = myDetail.championId,
                    puuid = myDetail.puuid,
                    season = detail.info.gameVersion.substring(0, 2),
                    queue = if (detail.info.queueId == 420) QueueType.RANKED_SOLO_5x5 else QueueType.RANKED_FLEX_SR
                )
            )
        }
        if (mySummonerChampionStatisticsList.size > 0) {
            summonerChampionStatisticsRepo.saveAll(mySummonerChampionStatisticsList)
        }
    }

    fun getMatchesByTypeAndDate(gameType: QueueType, until: Calendar): List<Match> {
        logger.info("getMatchesByTypeAndDate called")
        return matchRepo.findByGameCreationLessThanAndQueueId(until.timeInMillis, gameType.queueId)
    }

    fun getWinRatiosLastSevenDays(puuid: String): List<ChampionWinRateDto> {
        logger.info("getWinRatiosLastSevenDays")
        return getMatchesByTypeAndDate(QueueType.RANKED_SOLO_5x5, Calendar.getInstance()).flatMap { match ->
            match.matchSummoners.filter { it.puuid == puuid }.flatMap { matchSummoner ->
                match.matchTeams.filter { it.teamId == matchSummoner.teamId }.map { matchTeam ->
                    matchSummoner.matchSummonerChampion?.let {
                        Pair(it.championId, matchTeam.win)
                    } ?: throw Exception()
                }
            }
        }.fold(mutableMapOf<Int, ChampionWinRateDto>()) { container, data ->
            container.putIfAbsent(data.first!!, ChampionWinRateDto(data.first!!, 0, 0))
            if (data.second!!) {
                container[data.first!!]!!.wins++
            } else {
                container[data.first!!]!!.losses++
            }
            container
        }.values.toList()
    }
}