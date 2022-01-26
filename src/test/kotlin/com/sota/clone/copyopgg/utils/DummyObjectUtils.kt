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
    }
}
