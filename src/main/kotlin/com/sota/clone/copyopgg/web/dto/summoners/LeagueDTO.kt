package com.sota.clone.copyopgg.web.dto.summoners

import com.sota.clone.copyopgg.domain.models.QueueType
import com.sota.clone.copyopgg.domain.models.Rank
import com.sota.clone.copyopgg.domain.models.Tier

data class LeagueDTO(
    val leagueId: String,
    val tier: Tier?,
    val rank: Rank?,
    val queue: QueueType?,
    val name: String?,
    val leaguePoints: Int?,
)