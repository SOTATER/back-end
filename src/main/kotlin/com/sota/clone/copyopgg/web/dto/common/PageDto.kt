package com.sota.clone.copyopgg.web.dto.common

data class PageDto<T>(
    val page: Int,
    val hasNext: Boolean,
    val contents: List<T>
)