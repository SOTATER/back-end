package com.sota.clone.copyopgg.utils

import com.sota.clone.copyopgg.domain.entities.*
import com.sota.clone.copyopgg.web.dto.summoners.LeagueSummonerDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerInfoDTO
import org.apache.commons.lang3.RandomStringUtils
import kotlin.random.Random

class ConvertDataUtils {
    companion object {
        fun Summoner.toDTO() = SummonerDTO(
            accountId = accountId,
            profileIconId = profileIconId,
            revisionDate = revisionDate,
            name = name,
            id = id,
            puuid = puuid,
            summonerLevel = summonerLevel
        )

        fun Summoner.toInfoDTO() = SummonerInfoDTO(
            id = id,
            puuid = puuid,
            name = name,
            profileIconId = profileIconId,
            summonerLevel = summonerLevel,
            leagueInfo = null,
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

        fun Summoner.replaceName(name: String) = Summoner(
            accountId = accountId,
            profileIconId = profileIconId,
            revisionDate = revisionDate,
            name = name,
            id = id,
            puuid = puuid,
            summonerLevel = summonerLevel
        )

        fun LeagueSummoner.toDTO() = LeagueSummonerDTO(
            summonerId = summonerId,
            leagueId = leagueId,
            leaguePoints = leaguePoints,
            rank = rank,
            wins = wins,
            losses = losses,
            veteran = veteran,
            inactive = inactive,
            freshBlood = freshBlood,
            hotStreak = hotStreak,
        )

        fun LeagueSummonerDTO.toEntity() = LeagueSummoner(
            summonerId = summonerId,
            leagueId = leagueId,
            leaguePoints = leaguePoints ?: Random.nextInt(),
            rank = rank ?: Rank.I,
            wins = wins ?: Random.nextInt(),
            losses = losses ?: Random.nextInt(),
            veteran = veteran ?: Random.nextBoolean(),
            inactive = inactive ?: Random.nextBoolean(),
            freshBlood = freshBlood ?: Random.nextBoolean(),
            hotStreak = hotStreak ?: Random.nextBoolean(),
        )


        fun League.replaceQueueType(queue: QueueType) = League(
            leagueId = leagueId,
            tier = tier,
            queue = queue,
            name = name
        )
    }
}