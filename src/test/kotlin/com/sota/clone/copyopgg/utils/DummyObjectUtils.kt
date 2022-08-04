package com.sota.clone.copyopgg.utils

import com.sota.clone.copyopgg.domain.entities.*
import com.sota.clone.copyopgg.web.dto.summoners.LeagueDTO
import com.sota.clone.copyopgg.web.dto.summoners.QueueInfoDTO
import org.apache.commons.lang3.RandomStringUtils
import kotlin.random.Random

class DummyObjectUtils {
    companion object {
        fun getSummoner() = Summoner(
            accountId = RandomStringUtils.random(5, true, true),
            puuid = RandomStringUtils.random(5, true, true),
            id = RandomStringUtils.random(5, true, true),
            summonerLevel = Random.nextLong(),
            profileIconId = Random.nextInt(),
            name = RandomStringUtils.random(5, true, false),
            revisionDate = Random.nextLong()
        )

        fun getQueueInfoDTO() = QueueInfoDTO(
            summonerId = RandomStringUtils.random(5, true, true),
            leagueId = RandomStringUtils.random(5, true, true),
            tier = Tier.BRONZE,
            rank = Rank.I,
            leaguePoints = Random.nextInt(),
            wins = Random.nextInt(),
            losses = Random.nextInt(),
            leagueName = RandomStringUtils.random(5, true, true),
            queue = QueueType.RANKED_SOLO_5x5)

        fun getLeagueSummoner() = LeagueSummoner(
            summonerId = RandomStringUtils.random(5, true, true),
            leagueId = RandomStringUtils.random(5, true, true),
            leaguePoints = Random.nextInt(),
            rank = Rank.I,
            wins = Random.nextInt(),
            losses = Random.nextInt(),
            veteran = Random.nextBoolean(),
            inactive = Random.nextBoolean(),
            freshBlood = Random.nextBoolean(),
            hotStreak = Random.nextBoolean(),
        )

        fun getLeague() = League(
            leagueId = RandomStringUtils.random(5, true, true),
            tier = Tier.SILVER,
            queue = QueueType.RANKED_SOLO_5x5,
            name = RandomStringUtils.random(5, true, true),
        )

        fun getLeagueDTO() = LeagueDTO(
            leagueId = RandomStringUtils.random(5, true, true),
            tier = Tier.SILVER,
            queue = QueueType.RANKED_SOLO_5x5,
            rank = Rank.I,
            name = RandomStringUtils.random(5, true, true),
            leaguePoints = Random.nextInt()
        )

        fun getSummonerChampionStatistics(cid: Int, queue: Int) = SummonerChampionStatistics(
            minions_killed_all = Random.nextInt(),
            kills_all = Random.nextInt(),
            assists_all = Random.nextInt(),
            deaths_all = Random.nextInt(),
            played = Random.nextInt(),
            wins = Random.nextInt(),
            champion_id = cid,
            puuid = "test puuid",
            season = "test season",
            queue = if (queue == 0) QueueType.RANKED_SOLO_5x5 else QueueType.RANKED_FLEX_SR
        )

        fun getMatch(ms: MatchSummoner?, mt: MatchTeam?) = Match().apply {
            id = RandomStringUtils.random(5, true, true)
            dataVersion = RandomStringUtils.random(5, true, true)
            gameCreation = Random.nextLong()
            gameDuration = Random.nextLong()
            gameId = Random.nextLong()
            gameMode = GameMode.ARAM
            gameName = RandomStringUtils.random(5, true, true)
            gameStartTimestamp = Random.nextLong()
            gameType = GameType.MATCHED_GAME
            gameVersion = RandomStringUtils.random(5, true, true)
            mapId = Random.nextInt()
            platformId = RandomStringUtils.random(5, true, true)
            queueId = Random.nextInt()
            tournamentType = RandomStringUtils.random(5, true, true)
            matchSummoners = mutableListOf(ms ?: getMatchSummoner(this))
            matchTeams = mutableListOf(mt ?: getMatchTeam(this))
        }

        fun getMatchSummoner(m: Match?) = MatchSummoner().apply {
            id = Random.nextInt()
            match = m
            puuid = RandomStringUtils.random(5, true, true)
            gameEndedInEarlySurrender = false
            gameEndedInSurrender = false
            individualPosition = IndividualPosition.MIDDLE
            lane = LanePosition.MIDDLE
            participantId = Random.nextInt()
            riotIdName = RandomStringUtils.random(5, true, true)
            riotIdTagline = RandomStringUtils.random(5, true, true)
            role = MatchRole.SOLO
            teamEarlySurrendered = false
            teamId = Random.nextInt()
            teamPosition = LanePosition.MIDDLE
            matchSummonerChampion = getMatchSummonerChampion(this)
            matchSummonerPerk = getMatchSummonerPerk(this)
            matchSummonerCombat = getMatchSummonerCombat(this)
            matchSummonerObjective = getMatchSummonerObjective(this)
            matchSummonerVision = getMatchSummonerVision(this)
            matchSummonerItem = getMatchSummonerItem(this)
            matchSummonerRecord = getMatchSummonerRecord(this)
        }

        fun getMatchTeam(m: Match?) = MatchTeam().apply {
            id = Random.nextInt()
            match = m
            bans = mutableListOf(getBanInfo(this))
            teamId = Random.nextInt()
            win = true
            baronFirst = true
            baronKills = Random.nextInt()
            championFirst = true
            championKills = Random.nextInt()
            dragonFirst = Random.nextBoolean()
            dragonKills = Random.nextInt()
            inhibitorFirst = Random.nextBoolean()
            inhibitorKills = Random.nextInt()
            riftHeraldFirst = Random.nextBoolean()
            riftHeraldKills = Random.nextInt()
            towerFirst = Random.nextBoolean()
            towerKills = Random.nextInt()
        }

        fun getMatchSummonerChampion(ms: MatchSummoner) = MatchSummonerChampion().apply {
            matchSummoner = ms
            champExperience = Random.nextInt()
            championName = RandomStringUtils.random(5, true, true)
            championTransform = Random.nextInt()
            champLevel = Random.nextInt()
            championId = Random.nextInt()
            summoner1Casts = Random.nextInt()
            summoner1Id = Random.nextInt()
            summoner2Casts = Random.nextInt()
            summoner2Id = Random.nextInt()
        }

        fun getMatchSummonerPerk(ms: MatchSummoner) = MatchSummonerPerk().apply {
            id = Random.nextInt()
            matchSummoner = ms
            statPerksDefense = Random.nextInt()
            statPerksFlex = Random.nextInt()
            statPerksOffense = Random.nextInt()
            styles = mutableListOf(getMatchSummonerPerkStyle(this))
        }

        fun getMatchSummonerPerkStyle(msp: MatchSummonerPerk) = MatchSummonerPerkStyle().apply {
            id = Random.nextInt()
            perks = msp
            selections = mutableListOf(getMatchSummonerPerkStyleSelection(this))
            description = RandomStringUtils.random(5, true, true)
            style = Random.nextInt()
        }

        fun getMatchSummonerPerkStyleSelection(msps: MatchSummonerPerkStyle) = MatchSummonerPerkStyleSelection().apply {
            id = Random.nextInt()
            style = msps
            perk = Random.nextInt()
            var1 = Random.nextInt()
            var2 = Random.nextInt()
            var3 = Random.nextInt()
        }

        fun getMatchSummonerCombat(ms: MatchSummoner) = MatchSummonerCombat().apply {
            matchSummoner = ms
            kills = Random.nextInt()
            deaths = Random.nextInt()
            assists = Random.nextInt()
            firstBloodAssist = Random.nextBoolean()
            firstBloodKill = Random.nextBoolean()
            unrealKills = Random.nextInt()
            doubleKills = Random.nextInt()
            tripleKills = Random.nextInt()
            quadraKills = Random.nextInt()
            pentaKills = Random.nextInt()
            totalTimeCcDealt = Random.nextInt()
            timeCcingOthers = Random.nextInt()
            spell1Casts = Random.nextInt()
            spell2Casts = Random.nextInt()
            spell3Casts = Random.nextInt()
            spell4Casts = Random.nextInt()
            killingSprees = Random.nextInt()
            largestCriticalStrike = Random.nextInt()
            largestKillingSpree = Random.nextInt()
            largestMultiKill = Random.nextInt()
        }

        fun getMatchSummonerObjective(ms: MatchSummoner) = MatchSummonerObjective().apply {
            matchSummoner = ms
            turretKills = Random.nextInt()
            turretTakedowns = Random.nextInt()
            turretLost = Random.nextInt()
            neutralMinionsKilled = Random.nextInt()
            nexusKills = Random.nextInt()
            nexusLost = Random.nextInt()
            nexusTakedowns = Random.nextInt()
            objectiveStolen = Random.nextInt()
            objectivesStolenAssists = Random.nextInt()
            inhibitorKills = Random.nextInt()
            inhibitorTakedowns = Random.nextInt()
            inhibitorLost = Random.nextInt()
            firstTowerAssist = Random.nextBoolean()
            firstTowerKill = Random.nextBoolean()
            dragonKills = Random.nextInt()
            baronKills = Random.nextInt()
            totalMinionsKilled = Random.nextInt()
        }

        fun getMatchSummonerVision(ms: MatchSummoner) = MatchSummonerVision().apply {
            matchSummoner = ms
            visionWardsBoughtInGame = Random.nextInt()
            sightWardsBoughtInGame = Random.nextInt()
            visionScore = Random.nextInt()
            wardsKilled = Random.nextInt()
            wardsPlaced = Random.nextInt()
            detectorWardsPlaced = Random.nextInt()
        }

        fun getMatchSummonerItem(ms: MatchSummoner) = MatchSummonerItem().apply {
            matchSummoner = ms
            item0 = Random.nextInt()
            item1 = Random.nextInt()
            item2 = Random.nextInt()
            item3 = Random.nextInt()
            item4 = Random.nextInt()
            item5 = Random.nextInt()
            item6 = Random.nextInt()
            itemPurchased = Random.nextInt()
            goldEarned = Random.nextInt()
            goldSpent = Random.nextInt()
        }

        fun getMatchSummonerRecord(ms: MatchSummoner) = MatchSummonerRecord().apply {
            matchSummoner = ms
            totalDamageDealt = Random.nextInt()
            totalDamageDealtToChampions = Random.nextInt()
            totalDamageShieldedOnTeammates = Random.nextInt()
            totalDamageTaken = Random.nextInt()
            physicalDamageDealt = Random.nextInt()
            physicalDamageDealtToChampions = Random.nextInt()
            physicalDamageTaken = Random.nextInt()
            magicDamageDealt = Random.nextInt()
            magicDamageDealtToChampions = Random.nextInt()
            magicDamageTaken = Random.nextInt()
            trueDamageDealt = Random.nextInt()
            trueDamageDealtToChampions = Random.nextInt()
            trueDamageTaken = Random.nextInt()
            damageDealtToBuildings = Random.nextInt()
            damageDealtToObjectives = Random.nextInt()
            damageDealtToTurrets = Random.nextInt()
            damageSelfMitigated = Random.nextInt()
            timePlayed = Random.nextInt()
            totalHeal = Random.nextInt()
            totalHealOnTeammates = Random.nextInt()
            totalTimeSpentDead = Random.nextInt()
            totalUnitsHealed = Random.nextInt()
            longestTimeSpentLiving = Random.nextInt()
        }

        fun getBanInfo(mt: MatchTeam) = BanInfo().apply {
            id = Random.nextInt()
            matchTeam = mt
            championId = Random.nextInt()
            pickTurn = Random.nextInt()
        }
    }
}
