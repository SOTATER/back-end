package com.sota.clone.copyopgg.domain.dto

enum class TeamColor(
    private val color: String
) {
    RED("red"),
    BLUE("blue");

    fun getColor(): String {
        return color
    }
}