package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import com.sota.clone.copyopgg.utils.ConvertDataUtils.Companion.toDTO
import com.sota.clone.copyopgg.utils.DummyObjectUtils
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
import kotlin.test.assertNotEquals

@ExtendWith(MockKExtension::class)
class SynchronizeServiceTest {
    @MockK
    private lateinit var summonerRepository: SummonerRepository

    @MockK
    private lateinit var leagueSummonerRepository: LeagueSummonerRepository

    @MockK
    private lateinit var leagueRepository: LeagueRepository

    @MockK
    private lateinit var riotApiService: RiotApiService

    @MockK
    private lateinit var matchService: MatchService

    @InjectMockKs
    @SpyK
    private lateinit var synchronizeService: SynchronizeService

    @BeforeEach
    internal fun init() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    internal fun clear() {
        unmockkAll()
    }

    @Test
    fun `Test Refresh`() {
        // given
        val summoner = DummyObjectUtils.getSummoner()
        val leagueSummoner = DummyObjectUtils.getLeagueSummoner()
        val leagueDTO = DummyObjectUtils.getLeagueDTO()
        every { summonerRepository.findById(any()) } returns summoner
        every { riotApiService.getSummonerById(any()) } returns summoner.toDTO()
        every { riotApiService.getLeagueSummoners(any()) } returns arrayOf(leagueSummoner.toDTO())
        every { riotApiService.getLeague(any()) } returns leagueDTO
        every { summonerRepository.save(any()) } just Runs
        every { leagueSummonerRepository.save(any()) } just Runs
        every { leagueRepository.save(any()) } just Runs
        every { matchService.updateMatchesByPuuid(any()) } returns 1

        // when
        synchronizeService.refresh("tester")

        // verify
        verifySequence {
            summonerRepository.findById(any())
            riotApiService.getSummonerById(any())
            riotApiService.getLeagueSummoners(any())
            riotApiService.getLeague(any())
            summonerRepository.save(any())
            leagueSummonerRepository.save(any())
            leagueRepository.save(any())
            matchService.updateMatchesByPuuid(any())
        }

        verify(exactly = 1) {
            summonerRepository.findById(any())
            riotApiService.getSummonerById(any())
            riotApiService.getLeagueSummoners(any())
            riotApiService.getLeague(any())
            summonerRepository.save(any())
            leagueSummonerRepository.save(any())
            leagueRepository.save(any())
            matchService.updateMatchesByPuuid(any())
        }
    }

    @Test
    fun `Test Refresh Failed When summoner not in DB`() {
        // given
        every { summonerRepository.findById(any()) } returns null

        // when
        val actual = try {
            synchronizeService.refresh("tester")
            null
        } catch (e: Exception) {
            e
        }

        assertNotEquals(null, actual)

        // verify
        verifySequence {
            summonerRepository.findById(any())
        }

        verify(exactly = 1) { summonerRepository.findById(any()) }

        verify(exactly = 0) {
            riotApiService.getSummonerById(any())
            riotApiService.getLeagueSummoners(any())
            riotApiService.getLeague(any())
            summonerRepository.save(any())
            leagueSummonerRepository.save(any())
            leagueRepository.save(any())
            matchService.updateMatchesByPuuid(any())
        }
    }

    @Test
    fun `Test Refresh Failed When get summoner from riot failed`() {
        // given
        val summoner = DummyObjectUtils.getSummoner()
        every { summonerRepository.findById(any()) } returns summoner
        every { riotApiService.getSummonerById(any()) } returns null

        // when
        val actual = try {
            synchronizeService.refresh("tester")
            null
        } catch (e: Exception) {
            e
        }

        assertNotEquals(null, actual)

        // verify
        verifySequence {
            summonerRepository.findById(any())
            riotApiService.getSummonerById(any())
        }

        verify(exactly = 1) {
            summonerRepository.findById(any())
            riotApiService.getSummonerById(any())
        }

        verify(exactly = 0) {
            riotApiService.getLeagueSummoners(any())
            riotApiService.getLeague(any())
            summonerRepository.save(any())
            leagueSummonerRepository.save(any())
            leagueRepository.save(any())
            matchService.updateMatchesByPuuid(any())
        }
    }

    @Test
    fun `Test Refresh Failed When get league from riot failed`() {
        // given
        val summoner = DummyObjectUtils.getSummoner()
        val leagueSummoner = DummyObjectUtils.getLeagueSummoner()
        every { summonerRepository.findById(any()) } returns summoner
        every { riotApiService.getSummonerById(any()) } returns summoner.toDTO()
        every { riotApiService.getLeagueSummoners(any()) } returns arrayOf(leagueSummoner.toDTO())
        every { riotApiService.getLeague(any()) } returns null

        // when
        val actual = try {
            synchronizeService.refresh("tester")
            null
        } catch (e: Exception) {
            e
        }

        assertNotEquals(null, actual)

        // verify
        verifySequence {
            summonerRepository.findById(any())
            riotApiService.getSummonerById(any())
            riotApiService.getLeagueSummoners(any())
            riotApiService.getLeague(any())
        }

        verify(exactly = 1) {
            summonerRepository.findById(any())
            riotApiService.getSummonerById(any())
            riotApiService.getLeagueSummoners(any())
            riotApiService.getLeague(any())
        }

        verify(exactly = 0) {
            summonerRepository.save(any())
            leagueSummonerRepository.save(any())
            leagueRepository.save(any())
            matchService.updateMatchesByPuuid(any())
        }
    }
}