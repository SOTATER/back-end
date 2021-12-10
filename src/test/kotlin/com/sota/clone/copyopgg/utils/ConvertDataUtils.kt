package com.sota.clone.copyopgg.utils

import com.sota.clone.copyopgg.domain.models.League
import com.sota.clone.copyopgg.domain.models.LeagueSummoner
import com.sota.clone.copyopgg.domain.models.QueueType
import com.sota.clone.copyopgg.domain.models.Summoner
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
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

        fun League.replaceQueueType(queue: QueueType) = League(
            leagueId = leagueId,
            tier = tier,
            queue = queue,
            name = name
        )
    }
}