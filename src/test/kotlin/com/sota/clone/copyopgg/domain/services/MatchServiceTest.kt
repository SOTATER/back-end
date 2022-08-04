package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.dto.ChampionWinRateDto
import com.sota.clone.copyopgg.domain.entities.GameType
import com.sota.clone.copyopgg.domain.entities.QueueType
import com.sota.clone.copyopgg.domain.repositories.MatchRepository
import com.sota.clone.copyopgg.domain.repositories.MatchSummonerRepository
import com.sota.clone.copyopgg.domain.repositories.MatchTeamRepository
import com.sota.clone.copyopgg.utils.DummyObjectUtils
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class MatchServiceTest {
    @MockK
    private lateinit var matchRepository: MatchRepository

    @MockK
    private lateinit var matchSummonerRepository: MatchSummonerRepository

    @MockK
    private lateinit var matchTeamRepository: MatchTeamRepository

    @MockK
    private lateinit var riotApiService: RiotApiService

    @InjectMockKs
    @SpyK
    private lateinit var matchService: MatchService

    @BeforeEach
    internal fun init() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    internal fun clear() {
        unmockkAll()
    }

    @Test
    fun `Test GetWinRatiosLastSevenDays`() {
        // given
        val matchSummoner = DummyObjectUtils.getMatchSummoner(null)
        val matchTeam = DummyObjectUtils.getMatchTeam(null)

        matchSummoner.teamId = matchTeam.teamId
        matchSummoner.puuid = "testId"

        val match = DummyObjectUtils.getMatch(matchSummoner, matchTeam)

        match.queueId = QueueType.RANKED_SOLO_5x5.getQueueId()

        matchSummoner.match = match
        matchTeam.match = match

        every { matchRepository.findByGameCreationLessThan(any()) } returns listOf(match)

        val expected = listOf(ChampionWinRateDto(
            championId = matchSummoner.matchSummonerChampion!!.championId!!,
            wins = 1,
            losses = 0
        ))

        // when
        val actual = matchService.getWinRatiosLastSevenDays("testId")

        // verify
        assertEquals(expected, actual)
    }
}