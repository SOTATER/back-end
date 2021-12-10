package com.sota.clone.copyopgg.web.rest

import com.sota.clone.copyopgg.domain.entities.Match
import com.sota.clone.copyopgg.domain.services.MatchService
import com.sota.clone.copyopgg.domain.services.RiotApiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/matches")
class MatchController(
    @Autowired
    val riotApiService: RiotApiService,
    val matchService: MatchService
) {

    @GetMapping("/by-puuid/{puuid}/ids")
    fun getMatchIds(
        @PathVariable(value = "puuid") puuid: String,
        @RequestParam(required = false, value = "startTime") startTime: Long?,
        @RequestParam(required = false, value = "endTime") endTime: Long?,
        @RequestParam(required = false, value = "queue") queue: Int?,
        @RequestParam(required = false, value = "type") type: String?,
        @RequestParam(required = false, value = "start", defaultValue = "0") start: Int,
        @RequestParam(required = false, value = "count", defaultValue = "20") count: Int
    ): ResponseEntity<List<String>> {
        val params = RiotApiService.MatchIdsParams(
            startTime = startTime,
            endTime = endTime,
            queue = queue,
            type = type,
            start = start,
            count = count
        )
        val ids = riotApiService.getMatchIdsByPuuid(puuid, params)
        return ResponseEntity.ok(ids)
    }

    @GetMapping("/by-puuid/{puuid}")
    fun getMatches(
        @RequestParam(required = false, value = "startTime") startTime: Long,
        @RequestParam(required = false, value = "endTime") endTime: Long,
        @RequestParam(required = false, value = "queue") queue: Int,
        @RequestParam(required = false, value = "type") type: String,
        @RequestParam(required = false, value = "start", defaultValue = "0") start: Int,
        @RequestParam(required = false, value = "count", defaultValue = "20") count: Int
    ): ResponseEntity<List<Match>> {
        val fakerPuuid = "HOVpcdAqPb-2kFHuoCEHz4W5MPvRnaMNaIC25DgIY1-Aq4VAdMA9SRIMy01O9h1S43tiNc0JWjHNwA"

        return ResponseEntity.ok(listOf())
    }


}
