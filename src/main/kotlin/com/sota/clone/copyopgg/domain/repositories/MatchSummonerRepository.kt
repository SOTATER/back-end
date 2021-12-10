package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.MatchSummoner

interface MatchSummonerRepository {

    fun saveAll(matchSummoners: List<MatchSummoner>)

}