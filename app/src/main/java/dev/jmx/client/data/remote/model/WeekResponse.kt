package dev.jmx.client.data.remote.model

import dev.jmx.client.data.models.WeekData

data class WeekResponse(
    val categories: List<CategoryItem> = listOf(),
    val type: List<TypeItem>
) {
    data class CategoryItem(
        val id: String,
        val time: String,
        val title: String,
    )

    data class TypeItem(
        val id: String,
        val title: String
    )

    fun toWeekData() = WeekData(
        categoryList = categories.map { it.id to it.time },
        typeList = type.map { it.id to it.title }
    )
}