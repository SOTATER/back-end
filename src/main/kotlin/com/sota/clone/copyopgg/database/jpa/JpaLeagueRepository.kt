package com.sota.clone.copyopgg.database.jpa

import com.sota.clone.copyopgg.domain.entities.League
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class JpaLeagueRepository(
    @PersistenceContext
    val entityManager: EntityManager
) : LeagueRepository {
    val entityInformation = JpaEntityInformationSupport.getEntityInformation(League::class.java, entityManager)
    val logger: Logger = LoggerFactory.getLogger(JpaLeagueRepository::class.java)

    @Transactional(rollbackFor = [Exception::class])
    override fun save(league: League) {
        logger.info("save called")
        if (entityInformation.isNew(league)) {
            entityManager.persist(league)
        } else entityManager.merge(league)
    }

    override fun findById(id: String): League? {
        logger.info("findById called")
        return entityManager.find(League::class.java, id)
    }
}