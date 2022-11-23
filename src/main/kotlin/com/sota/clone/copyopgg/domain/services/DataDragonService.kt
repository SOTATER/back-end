package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.dto.DataDragonChampionData
import com.sota.clone.copyopgg.domain.dto.DataDragonSummonerSpellData
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
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

    private var versions = listOf<String>()

    /**
     * Data Dragon에서 소환사 주문 데이터를 가져온다.
     */
    fun getSummonerSpellData(version: String, languageCode: String): DataDragonSummonerSpellData {
        val url = UriComponentsBuilder.fromUriString(dataDragonUrl)
            .path("/cdn/$version/data/$languageCode/summoner.json")
            .build().toUriString()
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

    /**
     * Data Drgaon에서 챔피언 데이터를 가져온다.
     */
    fun getChampionData(version: String, languageCode: String): DataDragonChampionData {
        val url = UriComponentsBuilder.fromUriString(dataDragonUrl)
            .path("/cdn/$version/data/$languageCode/champion.json")
            .build().toUriString()
        val response = restTemplate.getForEntity(url, DataDragonChampionData::class.java)
        logger.debug("sending a request to data dragon - url: $url")
        if (response.statusCode != HttpStatus.OK) {
            throw Exception("Data dragon request failed - url: $url, status: ${response.statusCodeValue}")
        }
        if (response.hasBody()) {
            return response.body!!
        } else {
            throw Exception("Response has empty body.")
        }
    }

    /**
     * 버전 목록 조회.
     */
    fun getVersionsFromDataDragon(): List<String> {
        val url = UriComponentsBuilder.fromUriString(dataDragonUrl)
            .path("/api/versions.json")
            .build().toUriString()
        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<String>>() {}
        )
        if (response.statusCode != HttpStatus.OK) {
            throw Exception("Data dragon request failed - url: $url, status: ${response.statusCodeValue}")
        }
        if (response.hasBody()) {
            val ddVersions = response.body!!
            this.versions = ddVersions
            return versions
        } else {
            throw Exception("Response has empty body.")
        }
    }

    fun getAllVersions(): List<String> {
        if (this.versions.isEmpty()) {
            getVersionsFromDataDragon()
        }
        return versions
    }

    fun getDataDragonVersionFromGameVersion(gameVersion: String): String {
        val versionArr = gameVersion.split(".")
        return getAllVersions()
            .filter { it.startsWith("${versionArr[0]}.${versionArr[1]}") }
            .maxByOrNull { it.split(".")[2].toInt() }!!
    }
}