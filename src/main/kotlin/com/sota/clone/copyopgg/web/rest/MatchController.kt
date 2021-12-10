package com.sota.clone.copyopgg.web.rest

import com.sota.clone.copyopgg.domain.entities.Match
import com.sota.clone.copyopgg.domain.services.MatchService
import com.sota.clone.copyopgg.domain.services.RiotApiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/matches")
class MatchController(
    @Autowired
    val riotApiService: RiotApiService,
    val matchService: MatchService
) {

    @GetMapping()
    fun getMatches(@RequestParam puuid: String): ResponseEntity<Match> {
        val fakerPuuid = "HOVpcdAqPb-2kFHuoCEHz4W5MPvRnaMNaIC25DgIY1-Aq4VAdMA9SRIMy01O9h1S43tiNc0JWjHNwA"
        // riotApiService.getMatchHistory(fakerPuuid)
        // test
        matchService.test()
        return ResponseEntity.notFound().build()
    }
}
