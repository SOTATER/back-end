package com.sota.clone.copyopgg.web.rest

import com.sota.clone.copyopgg.domain.models.*
import com.sota.clone.copyopgg.domain.repositories.LeagueRepository
import com.sota.clone.copyopgg.domain.repositories.LeagueSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.SummonerRepository
import com.sota.clone.copyopgg.domain.services.RiotApiService
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
    private lateinit var summonerRepository: SummonerRepository

    @MockK
    private lateinit var leagueSummonerRepository: LeagueSummonerRepository

    @MockK
    private lateinit var leagueRepository: LeagueRepository

    @MockK
    private lateinit var riotApiController: RiotApiService

    @InjectMockKs
    @SpyK
    private lateinit var summonerController: SummonerController

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
    fun testGetSummonerInfoWhenSummonerInDB() {
        // given
        // Summoner Repository에서 db의 데이터를 리턴
        every { summonerRepository.searchByName(any<String>()) } returns getSummonerBriefInfo()

        // verify
        // getSummonerInfo에서 db의 데이터로부터 결과값 리턴 검증
        assert(summonerController.getSummonerInfo("tester") == ResponseEntity.ok().body(getSummonerBriefInfo()))
    }

    @Test
    fun testGetSummonerInfoWhenSummonerNotInDB() {
        // given
        // Summoner Repository에서 db에 데이터가 없으므로 null 리턴
        every { summonerRepository.searchByName(any<String>()) } returns null
        // db에 데이터가 없으므로, riot api를 통해 summoner 데이터 get
        every { riotApiController.getSummoner(any<String>()) } returns getSummonerDTO()
        // get한 summoner data는 db에 insert
        every { summonerRepository.insertSummoner(any<SummonerDTO>()) } just Runs


        // when
        // getSummonerInfo API 호출
        summonerController.getSummonerInfo("tester")

        // verify
        verify {
            // riot api 호출 검증
            riotApiController.getSummoner(any<String>())
            // db insert api 호출 검증
            summonerRepository.insertSummoner(any<SummonerDTO>())
        }
    }

    @Test
    fun testGetSummonerInfoWhenSummonerNotExists() {
        // given
        // db에 데이터가 없으므로 null 리턴
        every { summonerRepository.searchByName(any<String>()) } returns null
        // riot api로부터도 데이터가 없으므로 null 리턴
        every { riotApiController.getSummoner(any<String>()) } returns null

        // verify
        // getSummonerInfo API에서 빈 결과 리턴 검증
        assert(summonerController.getSummonerInfo("tester") == ResponseEntity<SummonerBriefInfo>(HttpStatus.NOT_FOUND))
    }

    @Test
    fun testGetSummonerLeagueInfoInDB() {
        // given
        // LeagueSummoner repo에서 db에 있는 값 리턴
        every { leagueSummonerRepository.getLeagueSummonerBySummonerId(any<String>()) } returns getLeagueSummoner()
        // League repo에서 db에 있는 값 리턴
        every { leagueRepository.findLeagueById(any<String>()) } returns getLeague()

        // verify
        // getBriefLeagueInfo 결과값 리턴 검증
        assert( summonerController.getBriefLeagueInfo("tester") == ResponseEntity.ok().body(getLeagueBriefInfo()))

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
        every { leagueRepository.findLeagueById(any<String>()) } returns getLeague()

        // verify
        // getBriefLeagueInfo에서 not found 리턴 (unranked) 처리
        assert( summonerController.getBriefLeagueInfo("tester") == ResponseEntity<LeagueBriefInfoBySummoner>(HttpStatus.NOT_FOUND))

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
        every { riotApiController.getLeague(any<String>()) } returns getLeague()
        every { riotApiController.getLeagueSummoner(any<String>()) } returns getLeagueSummoner()

        // when
        // refresh function 콜
        assert(summonerController.refresh("tester") == ResponseEntity.ok().body(BooleanResponse("tester", true)))

        // verify
        verifySequence {
            riotApiController.getLeagueSummoner(any<String>())
            riotApiController.getLeague(any<String>())
            leagueSummonerRepository.syncLeagueSummoner(any<LeagueSummoner>())
            leagueRepository.syncLeague(any<League>())
        }
        verify(exactly = 1) {
            riotApiController.getLeagueSummoner(any<String>())
            riotApiController.getLeague(any<String>())
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
        every { riotApiController.getLeague(any<String>()) } returns null
        every { riotApiController.getLeagueSummoner(any<String>()) } returns getLeagueSummoner()

        // when
        // refresh function 콜
        assert(summonerController.refresh("tester") == ResponseEntity.ok().body(BooleanResponse("tester", false)))

        // verify
        verify(exactly = 1) {
            riotApiController.getLeagueSummoner(any<String>())
            riotApiController.getLeague(any<String>())
        }

        verify(exactly = 0) {
            leagueSummonerRepository.syncLeagueSummoner(any<LeagueSummoner>())
            leagueRepository.syncLeague(any<League>())
        }
    }
}