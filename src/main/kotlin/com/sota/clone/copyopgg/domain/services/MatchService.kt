package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.dto.MatchDto
import com.sota.clone.copyopgg.domain.entities.Match
import com.sota.clone.copyopgg.domain.entities.SummonerChampionStatistics
import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.repositories.MatchRepository
import com.sota.clone.copyopgg.domain.repositories.MatchSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.MatchTeamRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerChampionStatisticsRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class MatchService(
    @Autowired val matchRepo: MatchRepository,
    @Autowired val matchSummonerRepo: MatchSummonerRepository,
    @Autowired val matchTeamRepo: MatchTeamRepository,
    @Autowired val summonerChampionStatisticsRepo: SummonerChampionStatisticsRepository,
    @Autowired val riotApiService: RiotApiService,
    val logger: Logger = LoggerFactory.getLogger(MatchService::class.java)
) {

    fun getMatchSummariesByPuuid(puuid: String, page: Int, pageSize: Int): List<Map<String, Any?>> {
        val pageable: Pageable = PageRequest.of(page, pageSize)
        val matchSummoners = matchSummonerRepo.findByPuuid(puuid, pageable)


        return List(matchSummoners.size) {
            val matchSummoner = matchSummoners[it]
            try {
                mapOf(
                    "champion" to matchSummoner.matchSummonerChampion!!.championId,
                    "spell1" to matchSummoner.matchSummonerChampion!!.summoner1Id,
                    "spell2" to matchSummoner.matchSummonerChampion!!.summoner2Id,
                    "kills" to matchSummoner.matchSummonerCombat!!.kills,
                    "deaths" to matchSummoner.matchSummonerCombat!!.deaths,
                    "assists" to matchSummoner.matchSummonerCombat!!.assists,
                    "level" to matchSummoner.matchSummonerChampion!!.champLevel,
                    "cs" to matchSummoner.matchSummonerObjective!!.totalMinionsKilled!! + matchSummoner.matchSummonerObjective!!.neutralMinionsKilled!!,
                    "item0" to matchSummoner.matchSummonerItem!!.item0,
                    "item1" to matchSummoner.matchSummonerItem!!.item1,
                    "item2" to matchSummoner.matchSummonerItem!!.item2,
                    "item3" to matchSummoner.matchSummonerItem!!.item3,
                    "item4" to matchSummoner.matchSummonerItem!!.item4,
                    "item5" to matchSummoner.matchSummonerItem!!.item5,
                    "item6" to matchSummoner.matchSummonerItem!!.item6,
                    "detectorWardsPlaced" to matchSummoner.matchSummonerVision!!.detectorWardsPlaced,
                )
            } catch (e: Error) {
                mapOf()
            }
        }
    }

    fun updateMatchesByPuuid(puuid: String): Int {
        // 현재 저장된 플레이어의 매치 중 가장 최신의 것의 id를 가져온다.
        val latestMatch = matchRepo.findLatestMatchByPuuid(puuid)
        // riot api를 통해 가져올 매치들의 id 목록 가져오기
        val ids = riotApiService.getMatchIdsByPuuid(
            puuid,
            RiotApiService.MatchIdsParams(
                startTime = latestMatch.gameStartTimestamp!! / 1000
            )
        )

        // 매치 id들을 이용하여 챔피언 통계 update
        updateSummonerChampionStatistics(ids, puuid)

        // Riot API를 통해 해당 매치 이후의 매치들을 가져온다
        ids.forEach { matchId ->
            if (matchId != latestMatch.id) migrateMatchData(matchId)
        }

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
        ids.forEach { matchId ->
            val detail = riotApiService.getMatchDetail(matchId)
            val myDetail = detail!!.info.participants.find({ i -> i.puuid == puuid})
            if (detail.info.queueId != 420 && detail.info.queueId != 440)
                return@forEach
            summonerChampionStatisticsRepo.save(
                SummonerChampionStatistics(
                    minions_killed_all = myDetail!!.totalMinionsKilled,
                    kills_all = myDetail.kills,
                    assists_all = myDetail.assists,
                    deaths_all = myDetail.deaths,
                    played = 1,
                    wins = if (myDetail.win) 1 else 0,
                    champion_id = myDetail.championId,
                    puuid = myDetail.puuid,
                    season = detail.info.gameVersion.substring(0,2),
                    queue = if(detail.info.queueId==420) QueueType.RANKED_SOLO_5x5 else QueueType.RANKED_FLEX_SR
                )
            )
        }
    }
}