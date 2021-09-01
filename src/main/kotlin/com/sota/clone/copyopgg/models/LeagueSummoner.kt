package com.sota.clone.copyopgg.models

data class LeagueSummoner(
    val summonerId: String,
    val leagueId: String,
    val leaguePoints: Int,
    val rank: Rank,
    val wins: Int,
    val loses: Int,
    val veteran: Boolean,
    val inactive: Boolean,
    val freshBlood: Boolean,
    val hotStreak: Boolean
)

data class LeagueBriefInfoBySummoner(
    val leaguePoints: Int,
    val wins: Int,
    val loses: Int,
    val tier: Tier?,
    val rank: Rank?,
    val leagueName: String,
)