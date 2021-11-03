package com.sota.clone.copyopgg.database.jpa

import com.sota.clone.copyopgg.domain.models.Summoner
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception
import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext

@Repository
class JpaSummonerRepository(
    @PersistenceContext
    val entityManager: EntityManager
): SummonerRepository {
    val logger = LoggerFactory.getLogger(SummonerRepository::class.java)

    @Transactional(rollbackFor = [Exception::class])
    override fun save(summoner: Summoner) {
        entityManager.persist(summoner)
    }

    override fun findByName(name: String): Summoner? {
        logger.info("findByName called on name=$name")
        val query = entityManager.createQuery(
            "select s from Summoner s " +
                    "where lower(replace(s.name, ' ', ''))=lower(replace(:searchName, ' ', ''))",
            Summoner::class.java
        )
        query.setParameter("searchName", name)
        return try {
            query.singleResult
        } catch (e: NoResultException) {
            null
        }
    }

    override fun findSummonersByPartialName(partialName: String, size: Int): List<Summoner> {
        if (size > 20) {
            throw IllegalArgumentException("Too many size to find summoner")
        }
        logger.info("findSummonersByPartialName called on partial name=$partialName")
        val query = entityManager.createQuery(
            "select s from Summoner s where " +
                    "lower(replace(s.name, ' ', '')) like " +
                    "concat('%',lower(replace(:partialName, ' ', '')),'%')",
            Summoner::class.java
        )
        query.setParameter("partialName", partialName)
        query.maxResults = size
        return query.resultList
    }
}