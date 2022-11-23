package com.sota.clone.copyopgg.domain.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.sota.clone.copyopgg.domain.entities.GameMode
import com.sota.clone.copyopgg.domain.entities.Match

enum class MatchType(
    val jsonValue: String
) {
    ARAM("aram"),
    SOLO_RANK("soloRank"),
    FREE_RANK("freeRank"),
    NORMAL("normal"),
    AI("ai"),
    EVENT("event"),
    CLASH("clash"),
    NONE("none");

    @JsonValue
    fun toJsonValue(): String? = jsonValuesMap.values.firstOrNull { it == this }?.jsonValue

    companion object {

        private val jsonValuesMap = values().associateBy(MatchType::jsonValue)

        @JsonCreator
        fun forJsonValue(jsonValue: String): MatchType {
            return jsonValuesMap[jsonValue] ?: NONE
        }
    }
}