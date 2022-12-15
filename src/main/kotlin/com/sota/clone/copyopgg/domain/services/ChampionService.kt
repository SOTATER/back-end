package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.dto.ChampionData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChampionService(
    @Autowired private val dataDragon: DataDragonService
) {

    private val championDataTable = mutableMapOf<String, MutableMap<String, Map<String, ChampionData>>>()
    private val championDataTableByIntKey = mutableMapOf<String, MutableMap<String, Map<Int, ChampionData>>>()

    fun getChampionData(version: String, languageCode: String): Map<String, ChampionData> {
        if (!championDataTable.containsKey(version) || !championDataTable[version]!!.containsKey(languageCode)) {
            storeChampionDataFromDataDragon(version, languageCode)
        }
        return championDataTable[version]!![languageCode]!!
    }

    fun getChampionDataWithIntKey(version: String, languageCode: String): Map<Int, ChampionData> {
        if (!championDataTable.containsKey(version) || !championDataTable[version]!!.containsKey(languageCode)) {
            storeChampionDataFromDataDragon(version, languageCode)
        }
        return championDataTableByIntKey[version]!![languageCode]!!
    }


    fun getChampionByChampionId(version: String, languageCode: String, championId: Int): ChampionData {
        return getChampionDataWithIntKey(version, languageCode)[championId]!!
    }

    private fun storeChampionDataFromDataDragon(version: String, languageCode: String) {
        val response = dataDragon.getChampionData(version, languageCode)
        if (championDataTable.containsKey(version)) {
            championDataTable[version]!![languageCode] = response.data
            championDataTableByIntKey[version]!![languageCode] = response.data.mapKeys { it.value.key.toInt() }
        } else {
            championDataTable[version] = mutableMapOf(languageCode to response.data)
            championDataTableByIntKey[version] =
                mutableMapOf(languageCode to response.data.mapKeys { it.value.key.toInt() })
        }
    }
}