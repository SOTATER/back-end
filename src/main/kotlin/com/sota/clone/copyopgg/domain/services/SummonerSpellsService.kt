package com.sota.clone.copyopgg.domain.services

import com.sota.clone.copyopgg.domain.dto.DataDragonSummonerSpellData
import com.sota.clone.copyopgg.domain.dto.SummonerSpellData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SummonerSpellsService(
    @Autowired
    private val dataDragon: DataDragonService
) {

    private val spellDataTable = mutableMapOf<String, MutableMap<String, Map<String, SummonerSpellData>>>()
    private val spellDataTableByIntKey = mutableMapOf<String, MutableMap<String, Map<Int, SummonerSpellData>>>()

    fun getSummonerSpellData(version: String, languageCode: String): Map<String, SummonerSpellData> {
        if (!spellDataTable.containsKey(version) || !spellDataTable[version]!!.containsKey(languageCode)) {
            storeSummonerSpellDataFromDataDragon(version, languageCode)
        }
        return spellDataTable[version]!![languageCode]!!
    }

    fun getSummonerSpellDataWithIntKey(version: String, languageCode: String): Map<Int, SummonerSpellData> {
        if (!spellDataTable.containsKey(version) || !spellDataTable[version]!!.containsKey(languageCode)) {
            storeSummonerSpellDataFromDataDragon(version, languageCode)
        }
        return spellDataTableByIntKey[version]!![languageCode]!!
    }

    fun getSummonerSpellStringId(version: String, languageCode: String, key: Int): String {
        return getSummonerSpellDataWithIntKey(version, languageCode)[key]!!.id
    }

    private fun storeSummonerSpellDataFromDataDragon(version: String, languageCode: String) {
        val response = dataDragon.getSummonerSpellData(version, languageCode)
        if (spellDataTable.containsKey(version)) {
            spellDataTable[version]!![languageCode] = response.data
            spellDataTableByIntKey[version]!![languageCode] = response.data.mapKeys { it.value.key.toInt() }
        } else {
            spellDataTable[version] = mutableMapOf(languageCode to response.data)
            spellDataTableByIntKey[version] =
                mutableMapOf(languageCode to response.data.mapKeys { it.value.key.toInt() })
        }
    }
}