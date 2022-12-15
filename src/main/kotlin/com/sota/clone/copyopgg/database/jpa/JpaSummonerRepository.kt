package com.sota.clone.copyopgg.database.jpa

import com.sota.clone.copyopgg.domain.entities.Summoner
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.EntityTransaction
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext

@Primary
@Repository
class JpaSummonerRepository(
    @PersistenceContext val entityManager: EntityManager,
) : SummonerRepository {
    val entityInformation = JpaEntityInformationSupport.getEntityInformation(Summoner::class.java, entityManager)
    val logger: Logger = LoggerFactory.getLogger(JpaSummonerRepository::class.java)

    @Transactional(rollbackFor = [Exception::class])
    override fun save(summoner: Summoner) {
        logger.info("save called")
        if (entityInformation.isNew(summoner)) {
            entityManager.persist(summoner)
        } else entityManager.merge(summoner)
    }

    override fun findById(id: String): Summoner? {
        logger.info("findById called")
        return entityManager.find(Summoner::class.java, id)
    }

    override fun findByPuuid(puuid: String): Summoner? {
        val query = entityManager.createQuery(
            "SELECT S FROM Summoner AS S " + "WHERE S.puuid = :puuid", Summoner::class.java
        )
        query.setParameter("puuid", puuid)
        return try {
            query.singleResult
        } catch (e: NoResultException) {
            null
        }
    }

    override fun findByName(name: String): Summoner? {
        logger.info("findByName called on name=$name")
        val query = entityManager.createQuery(
            "select s from Summoner s " + "where lower(replace(s.name, ' ', ''))=lower(replace(:searchName, ' ', ''))",
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
            "select s from Summoner s where " + "lower(replace(s.name, ' ', '')) like " + "concat('%',lower(replace(:partialName, ' ', '')),'%')",
            Summoner::class.java
        )
        query.setParameter("partialName", partialName)
        query.maxResults = size
        return query.resultList
    }

    override fun findByPuuids(puuids: List<String>): List<Summoner> {
        val query = entityManager.createQuery(
            "select s from Summoner s where s.puuid in :puuids", Summoner::class.java
        )
        query.setParameter("puuids", puuids)
        return query.resultList
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun saveAll(summoners: List<Summoner>) {
        for (summoner in summoners) {
            entityManager.persist(summoner)
        }
    }
}