package dev.jmx.client.data.remote.model

data class CommentAlbumResponse(
    val msg: String,
    val status: String,
    val aid: Int,
    val cid: Int,
    val spoiler: String,
)