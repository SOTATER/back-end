package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.dto.MatchDto
import com.sota.clone.copyopgg.domain.entities.Match
import com.sota.clone.copyopgg.domain.repositories.MatchRepository
import com.sota.clone.copyopgg.domain.repositories.MatchSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.MatchTeamRepository
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
    @Autowired val riotApiService: RiotApiService,
    val logger: Logger = LoggerFactory.getLogger(MatchService::class.java)
) {

    fun test() {
        val test = matchRepo.findAll()
    }

    fun getMatchesByPuuid(): List<MatchDto> {
        return listOf()
    }

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
}