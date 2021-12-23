package com.sota.clone.copyopgg.database.jpa

import com.sota.clone.copyopgg.domain.entities.MatchTeam
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaMatchTeamRepository: JpaRepository<MatchTeam, Int> {

}