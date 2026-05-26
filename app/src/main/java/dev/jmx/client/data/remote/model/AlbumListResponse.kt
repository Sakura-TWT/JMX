package dev.jmx.client.data.remote.model

import dev.jmx.client.data.models.Album

data class AlbumListResponse(
    val search_query: String,
    val total: String,
    val redirect_aid: String?,
    val content: List<ContentListItem>
) {
    data class ContentListItem(
        val id: String,
        val author: String,
        val description: String?,
        val name: String,
        val image: String,
        val category: Category,
        val category_sub: Category,
        val liked: Boolean,
        val is_favorite: Boolean,
        val update_at: Int,
    ) {
        data class Category(
            val id: String?,
            val title: String?
        )
    }

    fun toAlbumList(): List<Album> {
        return content.map {
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