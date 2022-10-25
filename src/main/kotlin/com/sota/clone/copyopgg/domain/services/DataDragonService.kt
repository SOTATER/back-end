package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.dto.DataDragonSummonerSpellData
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import javax.persistence.Cacheable

@Service
class DataDragonService(
    @Value("\${application.external.datadragon.url}")
    private val dataDragonUrl: String,
    private val restTemplate: RestTemplate
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Data Dragon에서 소환사 주문 데이터 가져오기
     */
    fun getSummonerSpellData(version: String, languageCode: String): DataDragonSummonerSpellData {
        val url = UriComponentsBuilder.fromUriString(dataDragonUrl)
            .path("/cdn")
            .path("/$version")
            .path("/data")
            .path("/$languageCode")
            .path("/summoner.json")
            .build()
            .toUriString()
        logger.debug("sending a request to data dragon - url: $url")
        val response = restTemplate.getForEntity(url, DataDragonSummonerSpellData::class.java)
        if (response.statusCode != HttpStatus.OK) {
            throw Exception("Data dragon request failed - url: $url, status: ${response.statusCodeValue}")
        }
        if (response.hasBody()) {
            return response.body!!
        } else {
            throw Exception("Response has empty body.")
        }
    }
}