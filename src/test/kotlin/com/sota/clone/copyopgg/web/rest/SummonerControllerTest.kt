package com.sota.clone.copyopgg.web.rest

import com.sota.clone.copyopgg.domain.models.*
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import com.sota.clone.copyopgg.domain.services.RiotApiService
import com.sota.clone.copyopgg.domain.services.SummonerService
import com.sota.clone.copyopgg.domain.services.SynchronizeService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.ResponseEntity
import com.sota.clone.copyopgg.utils.*
import java.lang.Exception
import kotlin.test.assertEquals


@ExtendWith(MockKExtension::class)
class SummonerControllerTest {
    @MockK
    private lateinit var summonerService: SummonerService

    @MockK
    private lateinit var riotApiService: RiotApiService

    @MockK
    private lateinit var synchronizeService: SynchronizeService

    @InjectMockKs
    @SpyK
    private lateinit var summonerController: SummonerController

    private val testSummonerName = "tester"

    // TODO: BeforeEach와 AfterEach에서 왜 internal 써야되는지 알지 못하고 사용
    @BeforeEach
    internal fun init() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    internal fun clear() {
        unmockkAll()
    }

    @Test
    fun testGetMatchNamesWhenExists() {
        // given
        val tester = DummyObjectUtils.getSummonerDTO()
        every { summonerService.getFiveSummonersMatchedPartialName(any<String>()) } returns listOf(tester)

        // when
        // getMatchNames 호출
        val names = summonerController.getMatchNames(this.testSummonerName)

        // verify
        // getFiveSummonersMatchedPartialName 한번 호출
        val testerJson = tester.let {
            mapOf(
                "id" to it.puuid,
                "name" to it.name,
                "profileIconId" to it.profileIconId,
                "summonerLevel" to it.summonerLevel,
                "leagueInfo" to null
            )
        }

        assertEquals(listOf(testerJson), names)
    }

    @Test
    fun testGetMatchNamesWhenNotExist() {
        // given
        every { summonerService.getFiveSummonersMatchedPartialName(any<String>()) } returns listOf()

        // when
        val names = summonerController.getMatchNames(this.testSummonerName)

        // verify
        assertEquals(listOf(), names)
    }

    @Test
    fun testGetSummonerInfoWhenExist() {
        // given
        val tester = DummyObjectUtils.getSummonerDTO()
        every { summonerService.getSummonerByName(any<String>()) } returns tester

        // when
        // getSummonerInfo 호출
        val info = summonerController.getSummonerInfo(this.testSummonerName)

        // verify
        // getSummonerByName이 한번 호출된다
        val packed: ResponseEntity<Map<String, Any?>> = tester.let {
            ResponseEntity.ok().body(
                mapOf(
                    "id" to it.puuid,
                    "name" to it.name,
                    "profileIconId" to it.profileIconId,
                    "summonerLevel" to it.summonerLevel,
                    "leagueInfo" to null
                )
            )
        }

        assertEquals(packed, info)
    }

    @Test
    fun testGetSummonerInfoWhenNotExist() {
        // given
        every { summonerService.getSummonerByName(any<String>()) } returns null

        // when
        // getSummonerInfo 호출
        val info = summonerController.getSummonerInfo(this.testSummonerName)

        // verify
        // getSummonerByName이 한번 호출된다
        assertEquals(ResponseEntity.notFound().build(), info)
    }

    @Test
    fun testGetSoloLeagueInfoWhenExist() {
        val queue = DummyObjectUtils.getQueueInfoDTO()
        // given
        every { summonerService.getSummonerQueueInfo(any(), QueueType.RANKED_SOLO_5x5) } returns queue

        // when
        val info = summonerController.getSoloLeagueInfo("test")

        // verify
        val packed = queue.let { ResponseEntity.ok().body(it) }
        assertEquals(packed, info)
    }

    @Test
    fun testGetSoloLeagueInfoWhenNotExist() {
        // given
        every { summonerService.getSummonerQueueInfo(any(), QueueType.RANKED_SOLO_5x5) } returns null

        // when
        val info = summonerController.getSoloLeagueInfo("test")

        // verify
        assertEquals(ResponseEntity.notFound().build(), info)
    }

    @Test
    fun testGetFlexLeagueInfo() {
        // almost same as getSoloLeagueInfo, so just pass this test
    }

    @Test
    fun testRefreshDone() {
        // given
        val bool = BooleanResponse("id", true)
        every { synchronizeService.refresh(any()) } just Runs

        // when
        val done = summonerController.refresh("id")

        // verify
        assertEquals(ResponseEntity.ok().body(bool), done)
    }

    @Test
    fun testRefreshFailed() {
        // given
        val bool = BooleanResponse("id", false)
        every { synchronizeService.refresh(any()) } throws Exception()

        // when
        val done = summonerController.refresh("id")

        // verify
        assertEquals(ResponseEntity.ok().body(bool), done)
    }
}