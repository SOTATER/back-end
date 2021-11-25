package com.sota.clone.copyopgg.web.rest

import com.sota.clone.copyopgg.domain.models.*
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import com.sota.clone.copyopgg.domain.services.RiotApiService
import com.sota.clone.copyopgg.domain.services.SummonerService
import com.sota.clone.copyopgg.web.rest.SummonerController
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.sota.clone.copyopgg.utils.*


@ExtendWith(MockKExtension::class)
class SummonerControllerTest {
    @MockK
    private lateinit var summonerService: SummonerService

    @MockK
    private lateinit var leagueSummonerRepository: LeagueSummonerRepository

    @MockK
    private lateinit var leagueRepository: LeagueRepository

    @MockK
    private lateinit var riotApiService: RiotApiService

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
    fun testGetMatchNames() {
        // given
        every { summonerService.getFiveSummonersMatchedPartialName(any<String>()) } returns listOf(DummyObjectUtils.getSummonerDTO())

        // when
        // getMatchNames 호출
        summonerController.getMatchNames(this.testSummonerName)

        // verify
        // getFiveSummonersMatchedPartialName 한번 호출
        verify(exactly = 1) {
            summonerService.getFiveSummonersMatchedPartialName(any<String>())
        }
    }

    @Test
    fun testGetSummonerInfo() {
        // given
        every { summonerService.getSummonerByName(any<String>()) } returns DummyObjectUtils.getSummonerDTO()

        // when
        // getSummonerInfo 호출
        summonerController.getSummonerInfo(this.testSummonerName)

        // verify
        // getSummonerByName이 한번 호출된다
        verify(exactly = 1) {
            summonerService.getSummonerByName(any<String>())
        }
    }

    @Test
    fun testGetSummonerLeagueInfoInDB() {
        // given
        // LeagueSummoner repo에서 db에 있는 값 리턴
        every { leagueSummonerRepository.getLeagueSummonerBySummonerId(any<String>()) } returns DummyObjectUtils.getLeagueSummoner()
        // League repo에서 db에 있는 값 리턴
        every { leagueRepository.findLeagueById(any<String>()) } returns DummyObjectUtils.getLeague()

        // verify
        // getBriefLeagueInfo 결과값 리턴 검증
        assert(
            summonerController.getBriefLeagueInfo("tester") == ResponseEntity.ok()
                .body(DummyObjectUtils.getLeagueBriefInfo())
        )

        // repo로부터 db 호출 검증
        verify {
            leagueSummonerRepository.getLeagueSummonerBySummonerId(any<String>())
            leagueRepository.findLeagueById(any<String>())
        }
    }

    @Test
    fun testGetSummonerLeagueInfoNotInDB() {
        // given
        // db에 데이터가 없으므로 null 리턴
        every { leagueSummonerRepository.getLeagueSummonerBySummonerId(any<String>()) } returns null
        every { leagueRepository.findLeagueById(any<String>()) } returns DummyObjectUtils.getLeague()

        // verify
        // getBriefLeagueInfo에서 not found 리턴 (unranked) 처리
        assert(summonerController.getBriefLeagueInfo("tester") == ResponseEntity<LeagueBriefInfoBySummoner>(HttpStatus.NOT_FOUND))

        // repo를 통해 db 호출했는지 검증
        verify {
            leagueSummonerRepository.getLeagueSummonerBySummonerId(any<String>())
        }
    }

    @Test
    fun testRefreshDone() {
        // given
        // match db sync 콜
        // league db sync 콜
        every { leagueRepository.syncLeague(any<League>()) } just runs
        // league_summoner db sync 콜
        every { leagueSummonerRepository.syncLeagueSummoner(any<LeagueSummoner>()) } just runs
        // riot api 콜
        every { riotApiService.getLeague(any<String>()) } returns DummyObjectUtils.getLeague()
        every { riotApiService.getLeagueSummoner(any<String>()) } returns DummyObjectUtils.getLeagueSummoner()

        // when
        // refresh function 콜
        assert(summonerController.refresh("tester") == ResponseEntity.ok().body(BooleanResponse("tester", true)))

        // verify
        verifySequence {
            riotApiService.getLeagueSummoner(any<String>())
            riotApiService.getLeague(any<String>())
            leagueSummonerRepository.syncLeagueSummoner(any<LeagueSummoner>())
            leagueRepository.syncLeague(any<League>())
        }
        verify(exactly = 1) {
            riotApiService.getLeagueSummoner(any<String>())
            riotApiService.getLeague(any<String>())
            leagueSummonerRepository.syncLeagueSummoner(any<LeagueSummoner>())
            leagueRepository.syncLeague(any<League>())
        }
    }

    @Test
    fun testRefreshFailed() {
        // given
        // match db sync 콜
        // league db sync 콜
        every { leagueRepository.syncLeague(any<League>()) } just runs
        // league_summoner db sync 콜
        every { leagueSummonerRepository.syncLeagueSummoner(any<LeagueSummoner>()) } just runs
        // riot api 콜
        every { riotApiService.getLeague(any<String>()) } returns null
        every { riotApiService.getLeagueSummoner(any<String>()) } returns DummyObjectUtils.getLeagueSummoner()

        // when
        // refresh function 콜
        assert(summonerController.refresh("tester") == ResponseEntity.ok().body(BooleanResponse("tester", false)))

        // verify
        verify(exactly = 1) {
            riotApiService.getLeagueSummoner(any<String>())
            riotApiService.getLeague(any<String>())
        }

        verify(exactly = 0) {
            leagueSummonerRepository.syncLeagueSummoner(any<LeagueSummoner>())
            leagueRepository.syncLeague(any<League>())
        }
    }
}