package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.dto.MatchDto
import com.sota.clone.copyopgg.domain.entities.Match
import com.sota.clone.copyopgg.domain.repositories.MatchRepository
import com.sota.clone.copyopgg.domain.repositories.MatchSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.MatchTeamRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MatchService(
    @Autowired val matchRepo: MatchRepository,
    @Autowired val matchSummonerRepo: MatchSummonerRepository,
    @Autowired val matchTeamRepo: MatchTeamRepository,
    @Autowired val riotApiService: RiotApiService
) {

    fun test() {
        val test = matchRepo.findAll()
    }

    fun getMatchesByPuuid(): List<MatchDto> {
        return listOf()
    }

    fun updateMatchesByPuuid(puuid: String) {
        // 현재 저장된 플레이어의 매치 중 가장 최신의 것의 id를 가져온다.
        val ids = riotApiService.getMatchIdsByPuuid(puuid, RiotApiService.MatchIdsParams())

        // Riot API를 통해 해당 매치 이후의 매치들을 가져온다
        val matchId = ids[0]
        // 매치 정보들을 DB에 삽입한다.

        migrateMatchData(matchId)
    }

    private fun migrateMatchData(matchId: String) {
        val matchDto = riotApiService.getMatchDetail(matchId)

        matchDto?.toMatch()?.let { match ->
            matchRepo.save(match)
            matchSummonerRepo.saveAll(match.matchSummoners)
            matchTeamRepo.saveAll(match.matchTeams)
        }
    }
}