package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.repositories.MatchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MatchService(
    @Autowired val matchRepo: MatchRepository
) {

    fun test() {
        val test = matchRepo.findAll()
        test.forEach {
            println(it)
        }
    }

}