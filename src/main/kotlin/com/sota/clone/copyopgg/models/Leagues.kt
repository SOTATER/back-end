package com.sota.clone.copyopgg.models

data class LeagueInfo(
    val leagueId: String,
    val tier: Tier,
    val rank: Rank,
    val leaguePoints: Int
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