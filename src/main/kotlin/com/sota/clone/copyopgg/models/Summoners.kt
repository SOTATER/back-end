package com.sota.clone.copyopgg.models

// data class we can get from riot summoner api
data class SummonerDTO(
    val accountId: String,
    val profileIconId: Int,
    val revisionDate: Long,
    val name: String,
    val id: String,
    val puuid: String,
    val summonerLevel: Long
)

// data class for search-by-name api response
data class SummonerBriefInfo(
    val id: String,
    val name: String,
    val summonerLevel: Long,
    val profileIconId: Int,
    val leagueInfo: LeagueInfo?,
)

data class LeagueInfo(
    val leagueId: String,
    val tier: Tier,
    val rank: Rank,
    val leaguePoints: Int
)

data class LeagueSummoner(
    val summonerId: String,
    val leagueId: String,
    val leaguePoints: Int,
    val wins: Int,
    val loses: Int,
    val veteran: Boolean,
    val inactive: Boolean,
    val freshBlood: Boolean,
    val hotStreak: Boolean
)

data class League(
    val leagueId: String,
    val tier: Tier,
    val rank: Rank,
    val queue: QueueType
)

enum class QueueType {
    RANKED_SOLO_5x5, RANKED_FLEX_SR, RANKED_FLEX_TT
}

enum class Tier {
    IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
}

enum class Rank {
    I, II, III, IV
}