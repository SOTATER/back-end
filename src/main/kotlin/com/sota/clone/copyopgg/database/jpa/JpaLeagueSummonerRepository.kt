package com.sota.clone.copyopgg.database.jpa

import com.sota.clone.copyopgg.domain.entities.LeagueSummoner
import com.sota.clone.copyopgg.domain.entities.LeagueSummonerPK
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Primary
@Repository
class JpaLeagueSummonerRepository(
    @PersistenceContext
    val entityManager: EntityManager
) : LeagueSummonerRepository {
    val entityInformation = JpaEntityInformationSupport.getEntityInformation(LeagueSummoner::class.java, entityManager)
    val logger: Logger = LoggerFactory.getLogger(JpaLeagueSummonerRepository::class.java)

    @Transactional(rollbackFor = [Exception::class])
    override fun save(leagueSummoner: LeagueSummoner) {
        logger.info("save called")
        if (entityInformation.isNew(leagueSummoner)) {
            entityManager.persist(leagueSummoner)
        } else entityManager.merge(leagueSummoner)
    }

    override fun findById(id: LeagueSummonerPK): LeagueSummoner? {
        logger.info("findById called")
        return entityManager.find(LeagueSummoner::class.java, id)
    }

    override fun findBySummonerId(summonerId: String): List<LeagueSummoner> {
        logger.info("findBySummonerId called")
        val query = entityManager.createQuery(
            "select ls from LeagueSummoner ls where ls.summonerId=:searchId",
            LeagueSummoner::class.java
        )
        query.setParameter("searchId", summonerId)
        return query.resultList
    }
}