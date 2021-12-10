package com.sota.clone.copyopgg.web.dto.summoners

import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.entities.Rank
import com.sota.clone.copyopgg.domain.entities.Tier

data class LeagueDTO(
    val leagueId: String,
    val tier: Tier?,
    val rank: Rank?,
    val queue: QueueType?,
    val name: String?,
    val leaguePoints: Int?,
)