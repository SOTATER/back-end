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
    @Autowired private val restTemplate: RestTemplate, @Value(value = "\${application.riotApiKey}") val apiKey: String
) {
    val logger: Logger = LoggerFactory.getLogger(RiotApiService::class.java)
    val apiRootUrl = "https://kr.api.riotgames.com"
    val matchBaseUrl = "https://asia.api.riotgames.com/lol/match/v5/matches"

    fun getSummoner(searchWord: String): SummonerDTO? = try {
        logger.info("get summoner named $searchWord via riot api")
        apiKey?.run {
            restTemplate.getForObject(
                "$apiRootUrl/lol/summoner/v4/summoners/by-name/$searchWord?api_key=$this", SummonerDTO::class.java
            )
        }
    } catch (e: Exception) {
        logger.error("error occurred when using riot api", e)
        null
    }

    fun getMatchTimeline() {

    }

    fun getSummonerByPuuid(puuid: String): SummonerDTO? {
        logger.debug("get summoner info by puuid")
        val headers = HttpHeaders()
        headers.set("X-Riot-Token", apiKey)

        // https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/cFcpZffw0RLy50IzULIeIJ5ekaQ0gLzfGS8WSM7gWbuNotf6GZDy9kULEDfgRaRR4kf9m_GuZxsUTw
        val uri =
            UriComponentsBuilder.fromHttpUrl("$apiRootUrl/lol/summoner/v4/summoners/by-puuid/$puuid").toUriString()
        val entity = HttpEntity<SummonerDTO?>(headers)
        return try {
            restTemplate.exchange<SummonerDTO>(
                uri, HttpMethod.GET, entity
            ).body
        } catch (e: Exception) {
            logger.debug("error occurred during Riot API call : ${e.message}")
            null
        }
    }

    fun getMatchDetail(matchId: String): MatchDto? {

        logger.info("Calling Riot API... GET match info")
        val headers = HttpHeaders()
        headers.set("X-Riot-Token", apiKey)

        val uriBuilder = UriComponentsBuilder.fromHttpUrl("$matchBaseUrl/$matchId")
        val entity = HttpEntity<Any?>(headers)
        val response = restTemplate.exchange<MatchDto>(
            uriBuilder.toUriString(), HttpMethod.GET, entity
        )

        return response.body
    }

    fun getMatchIdsByPuuid(puuid: String, params: MatchIdsParams?): List<String> {

        logger.info("Calling Riot API... GET matchIds by puuid")
        val headers = HttpHeaders()
        headers.set("X-Riot-Token", apiKey)

        val uriBuilder = params?.let {
            UriComponentsBuilder.fromHttpUrl("$matchBaseUrl/by-puuid/$puuid/ids").queryParam("startTime", it.startTime)
                .queryParam("endTime", it.endTime).queryParam("queue", it.queue).queryParam("type", it.type)
                .queryParam("start", it.start).queryParam("count", it.count)
        } ?: UriComponentsBuilder.fromHttpUrl("$matchBaseUrl/by-puuid/$puuid/ids")

        val entity = HttpEntity<Any?>(headers)

        val response = restTemplate.exchange<List<String>>(
            uriBuilder.toUriString(), HttpMethod.GET, entity
        )

        return response.body ?: listOf()
    }

    fun getSummonerById(searchId: String): SummonerDTO? = try {
        logger.info("get summoner named $searchId via riot api")
        apiKey?.run {
            restTemplate.getForObject(
                "$apiRootUrl/lol/summoner/v4/summoners/by-puuid/$searchId?api_key=$this", SummonerDTO::class.java
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
                    "$apiRootUrl/lol/league/v4/leagues/$leagueId?api_key=$this", LeagueDTO::class.java
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

    // Riot Match API Response Dto
    data class MatchDto(
        val metadata: MetadataDto, val info: InfoDto
    ) {
        fun toMatch(): Match {

            val match = Match()
            match.id = metadata.matchId
            match.dataVersion = metadata.dataVersion
            match.gameCreation = info.gameCreation
            match.gameDuration = info.gameDuration
            match.gameId = info.gameId
            match.gameMode = GameMode.valueOf(info.gameMode)
            match.gameName = info.gameName
            match.gameStartTimestamp = info.gameStartTimestamp
            match.gameType = GameType.valueOf(info.gameType)
            match.gameVersion = info.gameVersion
            match.mapId = info.mapId
            match.platformId = info.platformId
            match.queueId = info.queueId

            info.participants.forEach { participant ->

                val matchSummoner = MatchSummoner()
                matchSummoner.puuid = participant.puuid
                matchSummoner.gameEndedInEarlySurrender = participant.gameEndedInEarlySurrender
                matchSummoner.gameEndedInSurrender = participant.gameEndedInSurrender
                matchSummoner.individualPosition = IndividualPosition.fromName(participant.individualPosition)
                matchSummoner.lane = LanePosition.fromName(participant.lane)
                matchSummoner.participantId = participant.participantId
                matchSummoner.riotIdName = participant.riotIdName
                matchSummoner.riotIdTagline = participant.riotIdTagline
                matchSummoner.role = MatchRole.valueOf(participant.role)
                matchSummoner.teamEarlySurrendered = participant.teamEarlySurrendered
                matchSummoner.teamId = participant.teamId
                matchSummoner.teamPosition = LanePosition.fromName(participant.teamPosition)

                val matchSummonerChampion = MatchSummonerChampion()
                matchSummonerChampion.matchSummoner = matchSummoner
                matchSummonerChampion.champExperience = participant.champExperience
                matchSummonerChampion.championName = participant.championName
                matchSummonerChampion.championTransform = participant.championTransform
                matchSummonerChampion.champLevel = participant.champLevel
                matchSummonerChampion.championId = participant.championId
                matchSummonerChampion.summoner1Casts = participant.summoner1Casts
                matchSummonerChampion.summoner1Id = participant.summoner1Id
                matchSummonerChampion.summoner2Casts = participant.summoner2Casts
                matchSummonerChampion.summoner2Id = participant.summoner2Id

                val matchSummonerCombat = MatchSummonerCombat()
                matchSummonerCombat.matchSummoner = matchSummoner
                matchSummonerCombat.kills = participant.kills
                matchSummonerCombat.deaths = participant.deaths
                matchSummonerCombat.assists = participant.assists
                matchSummonerCombat.firstBloodAssist = participant.firstBloodAssist
                matchSummonerCombat.firstBloodKill = participant.firstBloodKill
                matchSummonerCombat.unrealKills = participant.unrealKills
                matchSummonerCombat.doubleKills = participant.doubleKills
                matchSummonerCombat.tripleKills = participant.tripleKills
                matchSummonerCombat.quadraKills = participant.quadraKills
                matchSummonerCombat.pentaKills = participant.pentaKills
                matchSummonerCombat.totalTimeCcDealt = participant.totalTimeCCDealt
                matchSummonerCombat.timeCcingOthers = participant.timeCCingOthers
                matchSummonerCombat.spell1Casts = participant.spell1Casts
                matchSummonerCombat.spell2Casts = participant.spell2Casts
                matchSummonerCombat.spell3Casts = participant.spell3Casts
                matchSummonerCombat.spell4Casts = participant.spell4Casts
                matchSummonerCombat.killingSprees = participant.killingSprees
                matchSummonerCombat.largestCriticalStrike = participant.largestCriticalStrike
                matchSummonerCombat.largestKillingSpree = participant.largestKillingSpree
                matchSummonerCombat.largestMultiKill = participant.largestMultiKill

                val matchSummonerItem = MatchSummonerItem()
                matchSummonerItem.matchSummoner = matchSummoner
                matchSummonerItem.item0 = participant.item0
                matchSummonerItem.item1 = participant.item1
                matchSummonerItem.item2 = participant.item2
                matchSummonerItem.item3 = participant.item3
                matchSummonerItem.item4 = participant.item4
                matchSummonerItem.item5 = participant.item5
                matchSummonerItem.item6 = participant.item6
                matchSummonerItem.itemPurchased = participant.itemsPurchased
                matchSummonerItem.consumablePurchased = participant.consumablesPurchased
                matchSummonerItem.goldEarned = participant.goldEarned
                matchSummonerItem.goldSpent = participant.goldSpent

                val matchSummonerObjective = MatchSummonerObjective()
                matchSummonerObjective.matchSummoner = matchSummoner
                matchSummonerObjective.turretKills = participant.turretKills
                matchSummonerObjective.turretTakedowns = participant.turretTakedowns
                matchSummonerObjective.turretLost = participant.turretLost
                matchSummonerObjective.neutralMinionsKilled = participant.neutralMinionsKilled
                matchSummonerObjective.nexusKills = participant.nexusKills
                matchSummonerObjective.nexusLost = participant.nexusLost
                matchSummonerObjective.nexusTakedowns = participant.nexusTakedowns
                matchSummonerObjective.objectiveStolen = participant.objectivesStolen
                matchSummonerObjective.objectivesStolenAssists = participant.objectivesStolenAssists
                matchSummonerObjective.inhibitorKills = participant.inhibitorKills
                matchSummonerObjective.inhibitorTakedowns = participant.inhibitorTakedowns
                matchSummonerObjective.inhibitorLost = participant.inhibitorLost
                matchSummonerObjective.firstTowerAssist = participant.firstTowerAssist
                matchSummonerObjective.firstTowerKill = participant.firstTowerKill
                matchSummonerObjective.dragonKills = participant.dragonKills
                matchSummonerObjective.baronKills = participant.baronKills
                matchSummonerObjective.totalMinionsKilled = participant.totalMinionsKilled

                val matchSummonerRecord = MatchSummonerRecord()
                matchSummonerRecord.matchSummoner = matchSummoner
                matchSummonerRecord.totalDamageDealt = participant.totalDamageDealt
                matchSummonerRecord.totalDamageDealtToChampions = participant.totalDamageDealtToChampions
                matchSummonerRecord.totalDamageShieldedOnTeammates = participant.totalDamageShieldedOnTeammates
                matchSummonerRecord.totalDamageTaken = participant.totalDamageTaken
                matchSummonerRecord.physicalDamageTaken = participant.physicalDamageTaken
                matchSummonerRecord.physicalDamageDealt = participant.physicalDamageDealt
                matchSummonerRecord.physicalDamageDealtToChampions = participant.physicalDamageDealtToChampions
                matchSummonerRecord.magicDamageDealt = participant.magicDamageDealt
                matchSummonerRecord.magicDamageDealtToChampions = participant.magicDamageDealtToChampions
                matchSummonerRecord.magicDamageTaken = participant.magicDamageTaken
                matchSummonerRecord.trueDamageDealt = participant.trueDamageDealt
                matchSummonerRecord.trueDamageDealtToChampions = participant.trueDamageDealtToChampions
                matchSummonerRecord.trueDamageTaken = participant.trueDamageTaken
                matchSummonerRecord.damageDealtToBuildings = participant.damageDealtToBuildings
                matchSummonerRecord.damageDealtToObjectives = participant.damageDealtToObjectives
                matchSummonerRecord.damageDealtToTurrets = participant.damageDealtToTurrets
                matchSummonerRecord.damageSelfMitigated = participant.damageSelfMitigated
                matchSummonerRecord.timePlayed = participant.timePlayed
                matchSummonerRecord.totalHeal = participant.totalHeal
                matchSummonerRecord.totalHealOnTeammates = participant.totalHealsOnTeammates
                matchSummonerRecord.totalTimeSpentDead = participant.totalTimeSpentDead
                matchSummonerRecord.totalUnitsHealed = participant.totalUnitsHealed
                matchSummonerRecord.longestTimeSpentLiving = participant.longestTimeSpentLiving

                val matchSummonerVision = MatchSummonerVision()
                matchSummonerVision.matchSummoner = matchSummoner
                matchSummonerVision.visionWardsBoughtInGame = participant.visionWardsBoughtInGame
                matchSummonerVision.sightWardsBoughtInGame = participant.sightWardsBoughtInGame
                matchSummonerVision.visionScore = participant.visionScore
                matchSummonerVision.wardsKilled = participant.wardsKilled
                matchSummonerVision.wardsPlaced = participant.wardsPlaced
                matchSummonerVision.detectorWardsPlaced = participant.detectorWardsPlaced

                val matchSummonerPerk = MatchSummonerPerk()
                matchSummonerPerk.matchSummoner = matchSummoner
                matchSummonerPerk.statPerksDefense = participant.perks.statPerks.defense
                matchSummonerPerk.statPerksFlex = participant.perks.statPerks.flex
                matchSummonerPerk.statPerksOffense = participant.perks.statPerks.offense
                participant.perks.styles.forEach {
                    val perkStyle = MatchSummonerPerkStyle()
                    perkStyle.perks = matchSummonerPerk
                    perkStyle.style = it.style
                    perkStyle.description = it.description
                    it.selections.forEach { sel ->
                        val selection = MatchSummonerPerkStyleSelection()
                        selection.perk = sel.perk
                        selection.var1 = sel.var1
                        selection.var2 = sel.var2
                        selection.var3 = sel.var3
                        perkStyle.addSelection(selection)
                    }
                    matchSummonerPerk.addPerkStyle(perkStyle)
                }

                matchSummoner.matchSummonerChampion = matchSummonerChampion
                matchSummoner.matchSummonerCombat = matchSummonerCombat
                matchSummoner.matchSummonerItem = matchSummonerItem
                matchSummoner.matchSummonerObjective = matchSummonerObjective
                matchSummoner.matchSummonerRecord = matchSummonerRecord
                matchSummoner.matchSummonerVision = matchSummonerVision
                matchSummoner.matchSummonerPerk = matchSummonerPerk

                match.addMatchSummoner(matchSummoner)
            }

            info.teams.forEach { dto ->
                val obj = dto.objectives
                val matchTeam = MatchTeam()
                dto.bans.forEach { ban ->
                    val banInfo = BanInfo()
                    banInfo.championId = ban.championId
                    banInfo.pickTurn = ban.pickTurn
                    matchTeam.addBanInfo(banInfo)
                }
                matchTeam.teamId = dto.teamId
                matchTeam.win = dto.win
                matchTeam.baronFirst = obj.baron.first
                matchTeam.baronKills = obj.baron.kills
                matchTeam.championFirst = obj.champion.first
                matchTeam.championKills = obj.champion.kills
                matchTeam.dragonFirst = obj.dragon.first
                matchTeam.dragonKills = obj.dragon.kills
                matchTeam.inhibitorFirst = obj.inhibitor.first
                matchTeam.inhibitorKills = obj.inhibitor.kills
                matchTeam.riftHeraldFirst = obj.riftHerald.first
                matchTeam.riftHeraldKills = obj.riftHerald.kills
                matchTeam.towerFirst = obj.tower.first
                matchTeam.towerKills = obj.tower.kills
                match.addMatchTeam(matchTeam)
            }

            return match
        }
    }

    data class MetadataDto(
        val dataVersion: String, val matchId: String, val participants: List<String>
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
        val participantId: Int,
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
        val statPerks: PerkStatsDto, val styles: List<PerkStyleDto>
    )

    data class PerkStatsDto(
        val defense: Int, val flex: Int, val offense: Int
    )

    data class PerkStyleDto(
        val description: String, val selections: List<PerkStyleSelectionDto>, val style: Int
    )

    data class PerkStyleSelectionDto(
        val perk: Int, val var1: Int, val var2: Int, val var3: Int
    )

    data class TeamDto(
        val bans: List<BanDto>, val objectives: ObjectivesDto, val teamId: Int, val win: Boolean
    )

    data class BanDto(
        val championId: Int, val pickTurn: Int
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
        val first: Boolean, val kills: Int
    )
}
