package com.sota.clone.copyopgg.domain.repositories

import com.sota.clone.copyopgg.domain.entities.MatchTeam

interface MatchTeamRepository {

    fun saveAll(matchTeams: List<MatchTeam>): List<MatchTeam>

}