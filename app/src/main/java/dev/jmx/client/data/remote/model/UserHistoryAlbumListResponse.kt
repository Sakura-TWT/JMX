package dev.jmx.client.data.remote.model

import dev.jmx.client.data.models.Album

data class UserHistoryAlbumListResponse (
    val list: List<ListItem>,
    val total: Int,
) {
    data class ListItem(
        val id: String,
        val author: String,
        val description: String?,
        val name: String,
        val image: String,
        val category: Category,
        val category_sub: Category,
    ) {
        data class Category(
            val id: String?,
            val title: String?
        )
    }

    fun toAlbumList(): List<Album> {
        return list.map {
            Album(
                id = it.id.toInt(),
                name = it.name,
                authorList = listOf(it.author),
                description = it.description ?: "",
                readCount = 0,
                likeCount = 0,
                commentCount = 0,
                tagList = listOf(),
                roleList = listOf(),
                workList = listOf(),
                isLike = false,
                isCollect = false,
                relateAlbumList = listOf(),
                albumChapterList = listOf(),
                price = 0,
                isBuy = false,
            )
        }
    }
}