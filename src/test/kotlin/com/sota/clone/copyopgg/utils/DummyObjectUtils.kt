package com.sota.clone.copyopgg.utils

import com.sota.clone.copyopgg.domain.models.*
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
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

        fun getSummonerDTO() = SummonerDTO(
            accountId = RandomStringUtils.random(5, true, true),
            puuid = RandomStringUtils.random(5, true, true),
            id = RandomStringUtils.random(5, true, true),
            summonerLevel = Random.nextLong(),
            profileIconId = Random.nextInt(),
            name = RandomStringUtils.random(5, true, false),
            revisionDate = Random.nextLong()
        )

        fun Summoner.toDTO() = SummonerDTO(
            accountId = accountId,
            profileIconId = profileIconId,
            revisionDate = revisionDate,
            name = name,
            id = id,
            puuid = puuid,
            summonerLevel = summonerLevel
        )

        fun SummonerDTO.toEntity() = Summoner(
            accountId = accountId ?: RandomStringUtils.random(5, true, true),
            profileIconId = profileIconId ?: Random.nextInt(),
            revisionDate = revisionDate ?: Random.nextLong(),
            name = name ?: RandomStringUtils.random(5, true, true),
            id = id ?: RandomStringUtils.random(5, true, true),
            puuid = puuid,
            summonerLevel = summonerLevel ?: Random.nextLong()
        )

        fun getLeagueSummoner() = LeagueSummoner(
            summonerId = "1234",
            leagueId = "1234",
            leaguePoints = 1234,
            rank = Rank.I,
            wins = 1234,
            losses = 1234,
            veteran = true,
            inactive = false,
            freshBlood = true,
            hotStreak = true,
        )

        fun getLeague() = League(
            leagueId = "1234",
            tier = Tier.SILVER,
            queue = QueueType.RANKED_SOLO_5x5,
            name = "1234"
        )

        fun getLeagueBriefInfo() = LeagueBriefInfoBySummoner(
            leaguePoints = 1234,
            wins = 1234,
            loses = 1234,
            tier = Tier.SILVER,
            rank = Rank.I,
            leagueName = "1234"
        )
    }
}
