package com.sota.clone.copyopgg.domain.dto

enum class TeamColor(
    val color: String,
    val id: Int
) {
    BLUE("blue", 100),
    RED("red", 200),
    NONE("none", -100);

    companion object {

        private val idMap = values().associateBy { it.id }

        fun fromId(id: Int?): TeamColor {
            if (id == null) {
                return NONE
            }
            return idMap[id] ?: NONE
        }
    }
}