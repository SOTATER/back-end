package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.models.League
import com.sota.clone.copyopgg.domain.models.QueueType
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import com.sota.clone.copyopgg.utils.ConvertDataUtils.Companion.replaceQueueType
import com.sota.clone.copyopgg.utils.ConvertDataUtils.Companion.toDTO
import com.sota.clone.copyopgg.utils.DummyObjectUtils
import com.sota.clone.copyopgg.web.dto.summoners.QueueInfoDTO
import com.sota.clone.copyopgg.web.dto.summoners.SummonerDTO
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
    private lateinit var summonerRepository: SummonerRepository

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
        val expected = listOf(testSummoner.toDTO())
        assertEquals(expected, actual)
    }

    @Test
    fun `Test GetFiveSummonersMatchedPartialName when no name which has such partial name exists`() {
        // given
        every { summonerRepository.findSummonersByPartialName(any(), any()) } returns listOf()

        // when
        val actual = summonerService.getFiveSummonersMatchedPartialName("tester")

        // verify
        val expected = listOf<SummonerDTO>()
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
        val expected = testSummoner.toDTO()
        assertEquals(expected, actual)
    }

    @Test
    fun `Test GetSummonerByName when no such summoner exists in DB`() {
        // given
        val testSummonerDTO = DummyObjectUtils.getSummonerDTO()
        every { summonerRepository.findByName(any()) } returns null
        every { summonerRepository.save(any()) } just Runs
        every { riotApiService.getSummoner(any()) } returns testSummonerDTO

        // when
        val actual = summonerService.getSummonerByName("tester")

        // verify
        assertEquals(expected = testSummonerDTO, actual)
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
        every { leagueSummonerRepository.findById(any(), any()) } returns leagueSummoner

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
        every { leagueSummonerRepository.findById(any(), any()) } returns leagueSummoner

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
}