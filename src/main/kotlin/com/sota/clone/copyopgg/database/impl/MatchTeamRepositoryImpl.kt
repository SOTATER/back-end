package com.sota.clone.copyopgg.database.impl

import com.sota.clone.copyopgg.database.jpa.JpaMatchTeamRepository
import com.sota.clone.copyopgg.domain.entities.MatchTeam
import com.sota.clone.copyopgg.domain.repositories.MatchTeamRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MatchTeamRepositoryImpl(
    @Autowired val jpaMatchTeamRepository: JpaMatchTeamRepository
): MatchTeamRepository {

    override fun saveAll(matchTeams: List<MatchTeam>): List<MatchTeam> {
        return jpaMatchTeamRepository.saveAll(matchTeams)
    }
}