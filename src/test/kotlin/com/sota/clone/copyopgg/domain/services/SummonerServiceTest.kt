package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.entities.SummonerChampionStatistics
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.MatchSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerChampionStatisticsRepository
import com.sota.clone.copyopgg.utils.ConvertDataUtils.Companion.replaceQueueType
import com.sota.clone.copyopgg.utils.ConvertDataUtils.Companion.toDTO
import com.sota.clone.copyopgg.utils.ConvertDataUtils.Companion.toInfoDTO
import com.sota.clone.copyopgg.utils.DummyObjectUtils
import com.sota.clone.copyopgg.web.dto.summoners.*
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class SummonerServiceTest {
    @MockK
    private lateinit var matchSummonerRepository: MatchSummonerRepository

    @MockK
    private lateinit var summonerRepository: SummonerRepository

    @MockK
    private lateinit var summonerChampionStatisticsRepository: SummonerChampionStatisticsRepository

    @MockK
    private lateinit var leagueSummonerRepository: LeagueSummonerRepository

    @MockK
    private lateinit var leagueRepository: LeagueRepository

    @MockK
    private lateinit var riotApiService: RiotApiService

    @InjectMockKs
    @SpyK
    private lateinit var summonerService: SummonerService

    @BeforeEach
    internal fun init() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    internal fun clear() {
        unmockkAll()
    }

    @Test
    fun `Test GetFiveSummonersMatchedPartialName`() {
        // given
        val testSummoner = DummyObjectUtils.getSummoner()
        every { summonerRepository.findSummonersByPartialName(any(), any()) } returns listOf(testSummoner)

        // when
        val actual = summonerService.getFiveSummonersMatchedPartialName("tester")

        // verify
        val expected = listOf(testSummoner.toInfoDTO())
        assertEquals(expected, actual)
    }

    @Test
    fun `Test GetFiveSummonersMatchedPartialName when no name which has such partial name exists`() {
        // given
        every { summonerRepository.findSummonersByPartialName(any(), any()) } returns listOf()

        // when
        val actual = summonerService.getFiveSummonersMatchedPartialName("tester")

        // verify
        val expected = listOf<SummonerInfoDTO>()
        assertEquals(expected, actual)
    }

    @Test
    fun `Test GetSummonerByName`() {
        // given
        val testSummoner = DummyObjectUtils.getSummoner()
        every { summonerRepository.findByName(any()) } returns testSummoner

        // when
        val actual = summonerService.getSummonerByName("tester")

        // verify
        val expected = testSummoner.toInfoDTO()
        assertEquals(expected, actual)
    }

    @Test
    fun `Test GetSummonerByName when no such summoner exists in DB`() {
        // given
        val testSummoner = DummyObjectUtils.getSummoner()
        every { summonerRepository.findByName(any()) } returns null
        every { summonerRepository.save(any()) } just Runs
        every { riotApiService.getSummoner(any()) } returns testSummoner.toDTO()

        // when
        val actual = summonerService.getSummonerByName("tester")

        // verify
        assertEquals(expected = testSummoner.toInfoDTO(), actual)
        verifySequence {
            summonerRepository.findByName(any())
            riotApiService.getSummoner(any())
            summonerRepository.save(any())
        }

        verify(exactly = 1) {
            summonerRepository.findByName(any())
            riotApiService.getSummoner(any())
            summonerRepository.save(any())
        }
    }

    @Test
    fun `Test GetSummonerByName when no such summoner exists`() {
        // given
        every { summonerRepository.findByName(any()) } returns null
        every { riotApiService.getSummoner(any()) } returns null

        // when
        val actual = summonerService.getSummonerByName("tester")

        // verify
        assertEquals(expected = null, actual)
        verify(exactly = 1) {
            summonerRepository.findByName(any())
            riotApiService.getSummoner(any())
        }
    }

    @Test
    fun `Test GetSummonerQueueInfo`() {
        // given
        val leagueSummoner = DummyObjectUtils.getLeagueSummoner()
        val summoners = listOf(leagueSummoner)
        val league = DummyObjectUtils.getLeague()
        every { leagueSummonerRepository.findBySummonerId(any()) } returns summoners
        every { leagueRepository.findById(any())} returns league
        every { leagueSummonerRepository.findById(any()) } returns leagueSummoner

        // when
        val actual = summonerService.getSummonerQueueInfo(leagueSummoner.summonerId, QueueType.RANKED_SOLO_5x5)

        // verify
        val expected = leagueSummoner.let {
            QueueInfoDTO(
                summonerId = it.summonerId,
                leagueId = it.leagueId,
                tier = league.tier,
                rank = it.rank,
                leaguePoints = it.leaguePoints,
                wins = it.wins,
                losses = it.losses,
                leagueName = league.name,
                queue = QueueType.RANKED_SOLO_5x5
            )
        }
        assertEquals(expected, actual)
    }

    @Test
    fun `Test GetSummonerQueueInfo when no such league type exists`() {
        // given
        val leagueSummoner = DummyObjectUtils.getLeagueSummoner()
        val summoners = listOf(leagueSummoner)
        val league = DummyObjectUtils.getLeague().replaceQueueType(QueueType.RANKED_FLEX_SR)
        every { leagueSummonerRepository.findBySummonerId(any()) } returns summoners
        every { leagueRepository.findById(any())} returns league
        every { leagueSummonerRepository.findById(any()) } returns leagueSummoner

        // when
        val actual = summonerService.getSummonerQueueInfo(leagueSummoner.summonerId, QueueType.RANKED_SOLO_5x5)

        // verify
        assertEquals(expected = null, actual)
    }

    @Test
    fun `Test GetSummonerQueueInfo when not exist league summoner at all`() {
        // given
        every { leagueSummonerRepository.findBySummonerId(any()) } returns listOf()

        // when
        val actual = summonerService.getSummonerQueueInfo("tester", QueueType.RANKED_SOLO_5x5)

        // verify
        assertEquals(expected = null, actual)
    }

    @Test
    fun `Test getSummonerChampionStatistics`() {
        // given
        val testSummoner = DummyObjectUtils.getSummoner()
        val puuid = testSummoner.puuid
        val testSummonerChampionStatistics = mutableListOf<SummonerChampionStatistics>()
        for (i in 0..15) {
            testSummonerChampionStatistics.add(DummyObjectUtils.getSummonerChampionStatistics(i,0))
            testSummonerChampionStatistics.add(DummyObjectUtils.getSummonerChampionStatistics(i,1))
        }

        every { summonerChampionStatisticsRepository.findByPuuidSeason(any(), any()) } returns testSummonerChampionStatistics

        // when
        val actual = summonerService.getSummonerChampionStatistics(puuid, "test season")

        // verify
        assertEquals(actual!!.rankedSoloFF!!.size, 7)
        assertEquals(actual.rankedFlexSR!!.size, 7)
        assertEquals(actual.total!!.size, 7)
        verify (exactly = 1) {
            summonerChampionStatisticsRepository.findByPuuidSeason(any(), any())
        }
    }

    @Test
    fun `Test getSummonerChampionStatistics when no exist SummonerChampionStatistics at all`() {
        // given
        val testSummoner = DummyObjectUtils.getSummoner()
        val puuid = testSummoner.puuid
        every { summonerChampionStatisticsRepository.findByPuuidSeason(any(), any())} returns listOf()

        // when
        val actual = summonerService.getSummonerChampionStatistics(puuid, "tester")

        // verify
        val expected = SummonerChampionStatisticsQueueDTO(
            listOf(),
            listOf(),
            listOf()
        )
        assertEquals(expected, actual)
        verify (exactly = 1) {
            summonerChampionStatisticsRepository.findByPuuidSeason(any(), any())
        }
    }
}