package com.sota.clone.copyopgg.repositories

import com.sota.clone.copyopgg.models.Match
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository

interface MatchRepository : CrudRepository<Match, String> {

}