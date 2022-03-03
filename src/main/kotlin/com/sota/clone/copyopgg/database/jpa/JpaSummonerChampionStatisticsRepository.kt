package com.sota.clone.copyopgg.database.jpa

import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.entities.SummonerChampionStatistics
import com.sota.clone.copyopgg.domain.entities.SummonerChampionStatisticsPK
import com.sota.clone.copyopgg.domain.repositories.SummonerChampionStatisticsRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class JpaSummonerChampionStatisticsRepository(
    @PersistenceContext
    val entityManager: EntityManager
) : SummonerChampionStatisticsRepository {
    val entityInformation = JpaEntityInformationSupport.getEntityInformation(SummonerChampionStatistics::class.java, entityManager)
    val logger: Logger = LoggerFactory.getLogger(JpaSummonerChampionStatisticsRepository::class.java)

    @Transactional(rollbackFor = [Exception::class])
    override fun save(summonerChampionStatistics: SummonerChampionStatistics) {
        logger.info("save called")
        if (entityInformation.isNew(summonerChampionStatistics)) {
            entityManager.persist(summonerChampionStatistics)
        } else {
            val findItem = this.findById(
                summonerChampionStatistics.champion_id,
                summonerChampionStatistics.puuid,
                summonerChampionStatistics.season,
                summonerChampionStatistics.queue)
            if (findItem != null)
                findItem.addGame(summonerChampionStatistics)
            else
                entityManager.merge(summonerChampionStatistics)
        }
    }

    override fun findByPuuidSeason(puuid: String, season: String): List<SummonerChampionStatistics> {
        logger.info("findByPuuidSeason called")
        val query = entityManager.createQuery(
            "select ls from SummonerChampionStatistics ls where ls.puuid=:puuid and ls.season=:season order by ls.queue, ls.played desc",
            SummonerChampionStatistics::class.java
        )
        query.setParameter("puuid", puuid)
        query.setParameter("season", season)
        return query.resultList
    }

    override fun findById(champion_id: Int, puuid: String, season: String, queue: QueueType): SummonerChampionStatistics? {
        logger.info("findById called")
        return entityManager.find(SummonerChampionStatistics::class.java, SummonerChampionStatisticsPK(champion_id, puuid, season, queue))
    }
}