package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.entities.*
import com.sota.clone.copyopgg.web.dto.summoners.LeagueDTO
import com.sota.clone.copyopgg.web.dto.summoners.LeagueSummonerDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder
import java.lang.Exception

@Service
class RiotApiService(
    @Autowired
    private val restTemplate: RestTemplate,
    @Value(value = "\${application.riotApiKey}")
    val apiKey: String
) {
    val logger: Logger = LoggerFactory.getLogger(RiotApiService::class.java)
    val apiRootUrl = "https://kr.api.riotgames.com"
    val matchBaseUrl = "https://asia.api.riotgames.com/lol/match/v5/matches"

    fun getSummoner(searchWord: String): SummonerDTO? = try {
        logger.info("get summoner named $searchWord via riot api")
        apiKey?.run {
            restTemplate.getForObject(
                "$apiRootUrl/lol/summoner/v4/summoners/by-name/$searchWord?api_key=$this",
                SummonerDTO::class.java
            )
        }
    } catch (e: Exception) {
        logger.error("error occurred when using riot api", e)
        null
    }

    fun getMatchTimeline() {

    }

    fun getMatchDetail(matchId: String): MatchDto? {

        val headers = HttpHeaders()
        headers.set("X-Riot-Token", apiKey)

        val uriBuilder = UriComponentsBuilder.fromHttpUrl("$matchBaseUrl/$matchId")
        val entity = HttpEntity<Any?>(headers)
        val response = restTemplate.exchange<MatchDto>(
            uriBuilder.toUriString(),
            HttpMethod.GET,
            entity
        )

        return response.body
    }

    fun getMatchIdsByPuuid(puuid: String, params: MatchIdsParams): List<String> {

        val headers = HttpHeaders()
        headers.set("X-Riot-Token", apiKey)

        val uriBuilder = UriComponentsBuilder.fromHttpUrl("$matchBaseUrl/by-puuid/$puuid/ids")
            .queryParam("startTime", params.startTime)
            .queryParam("endTime", params.endTime)
            .queryParam("queue", params.queue)
            .queryParam("type", params.type)
            .queryParam("start", params.start)
            .queryParam("count", params.count)

        val entity = HttpEntity<Any?>(headers)

        val response = restTemplate.exchange<List<String>>(
            uriBuilder.toUriString(),
            HttpMethod.GET,
            entity
        )

        return response.body ?: listOf()
    }

    fun getSummonerById(searchId: String): SummonerDTO? = try {
        logger.info("get summoner named $searchId via riot api")
        apiKey?.run {
            restTemplate.getForObject(
                "$apiRootUrl/lol/summoner/v4/summoners/by-puuid/$searchId?api_key=$this",
                SummonerDTO::class.java
            )
        }
    } catch (e: Exception) {
        logger.error("error occurred when using riot api", e)
        null
    }

    fun getLeague(leagueId: String): LeagueDTO? {
        logger.info("get league with id $leagueId via riot api")
        return try {
            apiKey?.run {
                restTemplate.getForObject(
                    "$apiRootUrl/lol/league/v4/leagues/$leagueId?api_key=$this",
                    LeagueDTO::class.java
                )
            }
        } catch (e: Exception) {
            logger.error("error occurred when using riot api", e)
            null
        }
    }

    fun getLeagueSummoners(summonerId: String): Array<LeagueSummonerDTO> {
        logger.info("get league of summoner with id $summonerId via riot api")
        return try {
            apiKey?.run {
                restTemplate.getForObject(
                    "$apiRootUrl/lol/league/v4/entries/by-summoner/$summonerId?api_key=$this",
                    Array<LeagueSummonerDTO>::class.java
                )
            } ?: arrayOf()
        } catch (e: Exception) {
            logger.error("error occurred when using riot api", e)
            arrayOf<LeagueSummonerDTO>()
        }
    }

    data class MatchIdsParams(
        val startTime: Long? = null,
        val endTime: Long? = null,
        val queue: Int? = null,
        val type: String? = null,
        val start: Int = 0,
        val count: Int = 20
    )
}

// Riot Match API Response Dto
data class MatchDto(
    val metadata: MetadataDto,
    val info: InfoDto
)

data class MetadataDto(
    val dataVersion: String,
    val matchId: String,
    val participants: List<String>
)

data class InfoDto(
    val gameCreation: Long,
    val gameDuration: Long,
    val gameEndTimestamp: Long,
    val gameId: Long,
    val gameMode: String,
    val gameName: String,
    val gameStartTimestamp: Long,
    val gameType: String,
    val gameVersion: String,
    val mapId: Int,
    val participants: List<ParticipantDto>,
    val platformId: String,
    val queueId: Int,
    val teams: List<TeamDto>,
    val tournamentCode: String
)

data class ParticipantDto(
    val assists: Int,
    val baronKills: Int,
    val bountyLevel: Int,
    val champExperience: Int,
    val champLevel: Int,
    val championId: Int,
    val championName: String,
    // 현재로선 케인 변신을 나타낼 때만 필요한 값이라고 한다.
    val championTransform: Int?,
    val consumablesPurchased: Int,
    val damageDealtToBuildings: Int,
    val damageDealtToObjectives: Int,
    val damageDealtToTurrets: Int,
    val damageSelfMitigated: Int,
    val deaths: Int,
    val detectorWardsPlaced: Int,
    val doubleKills: Int,
    val dragonKills: Int,
    val firstBloodAssist: Boolean,
    val firstBloodKill: Boolean,
    val firstTowerAssist: Boolean,
    val firstTowerKill: Boolean,
    val gameEndedInEarlySurrender: Boolean,
    val gameEndedInSurrender: Boolean,
    val goldEarned: Int,
    val goldSpent: Int,
    val individualPosition: String,
    val inhibitorKills: Int,
    val inhibitorTakedowns: Int,
    val inhibitorLost: Int,
    val item0: Int,
    val item1: Int,
    val item2: Int,
    val item3: Int,
    val item4: Int,
    val item5: Int,
    val item6: Int,
    val itemsPurchased: Int,
    val killingSprees: Int,
    val kills: Int,
    val lane: String,
    val largestCriticalStrike: Int,
    val largestKillingSpree: Int,
    val largestMultiKill: Int,
    val longestTimeSpentLiving: Int,
    val magicDamageDealt: Int,
    val magicDamageDealtToChampions: Int,
    val magicDamageTaken: Int,
    val neutralMinionsKilled: Int,
    val nexusKills: Int,
    val nexusTakedowns: Int,
    val nexusLost: Int,
    val objectivesStolen: Int,
    val objectivesStolenAssists: Int,
    val participantId: String,
    val pentaKills: Int,
    val perks: PerksDto,
    val physicalDamageDealt: Int,
    val physicalDamageDealtToChampions: Int,
    val physicalDamageTaken: Int,
    val profileIcon: Int,
    val puuid: String,
    val quadraKills: Int,
    val riotIdName: String,
    val riotIdTagline: String,
    val role: String,
    val sightWardsBoughtInGame: Int,
    val spell1Casts: Int,
    val spell2Casts: Int,
    val spell3Casts: Int,
    val spell4Casts: Int,
    val summoner1Casts: Int,
    val summoner1Id: Int,
    val summoner2Casts: Int,
    val summoner2Id: Int,
    val summonerId: String,
    val summonerLevel: Int,
    val summonerName: String,
    val teamEarlySurrendered: Boolean,
    val teamId: Int,
    val teamPosition: String,
    val timeCCingOthers: Int,
    val timePlayed: Int,
    val totalDamageDealt: Int,
    val totalDamageDealtToChampions: Int,
    val totalDamageShieldedOnTeammates: Int,
    val totalDamageTaken: Int,
    val totalHeal: Int,
    val totalHealsOnTeammates: Int,
    val totalMinionsKilled: Int,
    val totalTimeCCDealt: Int,
    val totalTimeSpentDead: Int,
    val totalUnitsHealed: Int,
    val tripleKills: Int,
    val trueDamageDealt: Int,
    val trueDamageDealtToChampions: Int,
    val trueDamageTaken: Int,
    val turretKills: Int,
    val turretTakedowns: Int,
    val turretLost: Int,
    val unrealKills: Int,
    val visionScore: Int,
    val visionWardsBoughtInGame: Int,
    val wardsKilled: Int,
    val wardsPlaced: Int,
    val win: Boolean
)

data class PerksDto(
    val statPerks: PerkStatsDto,
    val styles: List<PerkStyleDto>
)

data class PerkStatsDto(
    val defense: Int,
    val flex: Int,
    val offense: Int
)

data class PerkStyleDto(
    val description: String,
    val selections: List<PerkStyleSelectionDto>,
    val style: Int
)

data class PerkStyleSelectionDto(
    val perk: Int,
    val var1: Int,
    val var2: Int,
    val var3: Int
)

data class TeamDto(
    val bans: List<BanDto>,
    val objectives: ObjectivesDto,
    val teamId: Int,
    val win: Boolean
)

data class BanDto(
    val championId: Int,
    val pickTurn: Int
)

data class ObjectivesDto(
    val baron: ObjectiveDto,
    val champion: ObjectiveDto,
    val dragon: ObjectiveDto,
    val inhibitor: ObjectiveDto,
    val riftHerald: ObjectiveDto,
    val tower: ObjectiveDto
)

data class ObjectiveDto(
    val first: Boolean,
    val kills: Int
)