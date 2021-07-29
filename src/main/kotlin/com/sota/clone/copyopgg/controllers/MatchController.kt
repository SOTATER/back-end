package com.sota.clone.copyopgg.controllers

import com.sota.clone.copyopgg.repositories.MatchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/matches")
class MatchController(
    @Autowired matchRepo: MatchRepository
) {

}