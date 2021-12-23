package com.sota.clone.copyopgg.controllers

import com.sota.clone.copyopgg.domain.entities.*
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
        every { summonerRepository.searchByName(any<String>()) } returns this.getSummonerBriefInfo(true)

        // verify
        // getSummonerInfo에서 db의 데이터로부터 결과값 리턴 검증
        assert(summonerController.getSummonerInfo("tester") == ResponseEntity.ok().body(this.getSummonerBriefInfo(true)))
    }

    @Test
    fun testGetSummonerInfoWhenSummonerNotInDB() {
        // given
        // Summoner Repository에서 db에 데이터가 없으므로 null 리턴
        every { summonerRepository.searchByName(any<String>()) } returns null
        // db에 데이터가 없으므로, riot api를 통해 summoner 데이터 get
        every { riotApiController.getSummoner(any<String>()) } returns this.getSummonerDTO()
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
        every { leagueSummonerRepository.getLeagueSummonerBySummonerId(any<String>()) } returns this.getLeagueSummoner()
        // League repo에서 db에 있는 값 리턴
        every { leagueRepository.findLeagueById(any<String>()) } returns this.getLeague()

        // verify
        // getBriefLeagueInfo 결과값 리턴 검증
        assert( summonerController.getBriefLeagueInfo("tester") == ResponseEntity.ok().body(this.getLeagueBriefInfo()))

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
        every { leagueRepository.findLeagueById(any<String>()) } returns this.getLeague()

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
        every { riotApiController.getLeague(any<String>()) } returns this.getLeague()
        every { riotApiController.getLeagueSummoner(any<String>()) } returns this.getLeagueSummoner()

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
        every { riotApiController.getLeagueSummoner(any<String>()) } returns this.getLeagueSummoner()

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

    private fun getSummonerBriefInfo(succeed: Boolean) = if (succeed) SummonerBriefInfo(
        id = "test_id",
        name = "tester",
        profileIconId = 1234,
        summonerLevel = 123,
        leagueInfo = null
    ) else SummonerBriefInfo(
        id = "",
        name = "",
        profileIconId = -1,
        summonerLevel = -1,
        leagueInfo = null
    )

    private fun getSummonerDTO() = SummonerDTO(
        accountId = "1234",
        puuid = "1234",
        id = "1234",
        summonerLevel = 1234,
        profileIconId = 1234,
        name = "tester",
        revisionDate = 1234
    )

    private fun getLeagueSummoner() = LeagueSummoner(
        summonerId = "1234",
        leagueId = "1234",
        leaguePoints = 1234,
        rank = Rank.I,
        wins = 1234,
        losses = 1234,
        veteran = true,
        inactive = false,
        freshBlood = true,
        hotStreak = true,
    )

    private fun getLeague() = League(
        leagueId = "1234",
        tier = Tier.SILVER,
        queue = QueueType.RANKED_SOLO_5x5,
        name = "1234"
    )

    private fun getLeagueBriefInfo() = LeagueBriefInfoBySummoner(
        leaguePoints = 1234,
        wins = 1234,
        loses = 1234,
        tier = Tier.SILVER,
        rank = Rank.I,
        leagueName = "1234"
    )
}