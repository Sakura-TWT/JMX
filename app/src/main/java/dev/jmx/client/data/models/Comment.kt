package dev.jmx.client.data.models

data class Comment(
    val userId: Int,
    val albumId: Int,
    val id: Int,
    val time: String,
    val content: String,
    val likeCount: Int,
    val username: String,
    val nickname: String,
    val avatar: String,
    val parentId: Int,
    val spoiler: Boolean,
    val replyCommentList: List<Comment>
)