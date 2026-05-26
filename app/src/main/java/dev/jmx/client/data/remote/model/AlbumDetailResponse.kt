package dev.jmx.client.data.remote.model

import dev.jmx.client.data.models.Album
import dev.jmx.client.data.models.AlbumChapter

data class AlbumDetailResponse(
    val id: Int,
    val name: String,
    val description: String,
    val author: List<String>,
    val total_views: Int,
    val likes: Int,
    val comment_total: Int,
    val tags: List<String>,
    val actors: List<String>,
    val works: List<String>,
    val is_favorite: Boolean,
    val liked: Boolean,
    val related_list: List<AlbumDetailRelatedListItemResponse>,
    val series: List<AlbumDetailSeriesListItemResponse>,
    val series_id: String,
    val price: String,
    val purchased: Boolean,
) {
    fun toAlbum(): Album {
        return Album(
            id = id,
            name = name,
            authorList = author,
            description = description,
            readCount = total_views,
            likeCount = likes,
            commentCount = comment_total,
            tagList = tags,
            roleList = actors,
            workList = works,
            isLike = liked,
            isCollect = is_favorite,
            relateAlbumList = related_list.map {
                Album.create(
                    it.id.toInt(),
                    it.name,
                    listOf(it.author)
                )
            },
            albumChapterList = series.map { AlbumChapter(it.id.toInt(), it.name) },
            price = price.toIntOrNull() ?: 0,
            isBuy = purchased
        )
    }
}