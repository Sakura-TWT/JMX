package dev.jmx.client.data.remote.model

import dev.jmx.client.data.models.Album
import dev.jmx.client.data.models.HomeAlbumSwiperItem

class HomeSwiperAlbumListItemResponse(
    val id: String,
    val title: String,
    val slug: String,
    val type: String,
    val filter_val: String,
    val content: List<ListItem>
) {
    data class ListItem(
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

    fun toHomeAlbumSwiperItem(): HomeAlbumSwiperItem {
        return HomeAlbumSwiperItem(
            id = id,
            title = title,
            list = content.map {
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
        )
    }
}