package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import com.sota.clone.copyopgg.utils.DummyObjectUtils
import com.sota.clone.copyopgg.utils.DummyObjectUtils.Companion.toDTO
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

@ExtendWith(MockKExtension::class)
class SummonerServiceTest {
    @MockK
    private lateinit var summonerRepository: SummonerRepository

    @MockK
    private lateinit var riotApiService: RiotApiService

    @InjectMockKs
    @SpyK
    private lateinit var summonerService: SummonerService

    private val testSummonerName = "tester"

    @BeforeEach
    internal fun init() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    internal fun clear() {
        unmockkAll()
    }

    @Test
    fun testGetFiveSummonersMatchedPartialName() {
        // given
        val testSummoner = DummyObjectUtils.getSummoner()
        val testSummonerDTO = testSummoner.toDTO()
        every { summonerRepository.findSummonersByPartialName(any(), any()) } returns listOf(testSummoner)

        // verify
        assert(
            summonerService.getFiveSummonersMatchedPartialName(this.testSummonerName) == listOf(testSummonerDTO)
        )
    }

    @Test
    fun testGetFiveSummonersMatchedPartialNameIfNotExists() {
        // given
        every { summonerRepository.findSummonersByPartialName(any(), any()) } returns listOf()

        // verify
        assert(
            summonerService.getFiveSummonersMatchedPartialName(this.testSummonerName) == listOf<SummonerDTO>()
        )

        // verify
        verify(exactly = 1) {
            summonerRepository.findSummonersByPartialName(any(), any())
        }
    }

    @Test
    fun testGetSummonerByNameWhenExist() {
        // given
        val testSummoner = DummyObjectUtils.getSummoner()
        val testSummonerDTO = testSummoner.toDTO()
        every { summonerRepository.findByName(any()) } returns testSummoner

        // verify
        assert(
            summonerService.getSummonerByName(this.testSummonerName) == testSummonerDTO
        )

        verify(exactly = 0) {
            riotApiService.getSummoner(any())
        }
    }

    @Test
    fun testGetSummonerByNameWhenNotExistInDB() {
        // given
        val testSummonerDTO = DummyObjectUtils.getSummonerDTO()
        every { summonerRepository.findByName(any()) } returns null
        every { summonerRepository.save(any()) } just Runs
        every { riotApiService.getSummoner(any()) } returns testSummonerDTO

        // verify
        assert(
            summonerService.getSummonerByName(this.testSummonerName) == testSummonerDTO
        )

        verifySequence {
            summonerRepository.findByName(any())
            riotApiService.getSummoner(any())
            summonerRepository.save(any())
        }

        verify(exactly = 1) {
            riotApiService.getSummoner(any())
            summonerRepository.save(any())
        }
    }

    @Test
    fun testGetSummonerByNameWhenNotExistAtAll() {
        // given
        every { summonerRepository.findByName(any()) } returns null
        every { riotApiService.getSummoner(any()) } returns null

        // verify
        assert(
            summonerService.getSummonerByName(this.testSummonerName) == null
        )
    }
}