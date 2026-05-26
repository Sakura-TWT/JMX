package dev.jmx.client.data.models

data class WeekData(
    val categoryList: List<Pair<String, String>> = listOf(),
    val typeList: List<Pair<String, String>> = listOf()
)