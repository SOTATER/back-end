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
    val tier: Tier,
    val rank: Rank,
    val leaguePoints: Int
)

// data class for get-summoners-info response
data class SummonerProfileInfo(
    val puuid: String,
    val name: String,
    val summonerLevel: Long,
    val profileIconId: Int
)

enum class Tier {
    IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
}

enum class Rank {
    I, II, III, IV
}