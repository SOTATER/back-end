package com.sota.clone.copyopgg.utils

import com.sota.clone.copyopgg.models.*

fun getSummonerDTO() = SummonerDTO(
    accountId = "1234",
    puuid = "1234",
    id = "1234",
    summonerLevel = 1234,
    profileIconId = 1234,
    name = "tester",
    revisionDate = 1234
)

fun getSummonerBriefInfo() = SummonerBriefInfo(
    id = "test_id",
    name = "tester",
    profileIconId = 1234,
    summonerLevel = 123,
    leagueInfo = null
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
