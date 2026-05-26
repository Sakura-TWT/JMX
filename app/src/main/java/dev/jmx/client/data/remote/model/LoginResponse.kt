package dev.jmx.client.data.remote.model

import dev.jmx.client.data.models.User

data class LoginResponse(
    val uid: String? = null,
    val username: String? = null,
    val email: String? = null,
    val photo: String? = null,
    val s: String? = null,
    val coin: String? = null,
    val album_favorites: Int? = null,
    val level_name: String? = null,
    val level: Int? = null,
    val nextLevelExp: Int? = null,
    val exp: Int? = null,
    val expPercent: Double? = null,
    val album_favorites_max: Int? = null,
) {
    fun toUser(password: String): User = User(
        id = uid?.toIntOrNull() ?: 0,
        username = username.orEmpty(),
        password = password,
        avatar = photo.orEmpty(),
        level = level ?: 0,
        levelName = level_name.orEmpty(),
        currentLevelExp = exp ?: 0,
        nextLevelExp = nextLevelExp ?: 0,
        currentCollectCount = album_favorites ?: 0,
        maxCollectCount = album_favorites_max ?: 0,
        jCoin = coin?.toIntOrNull() ?: 0,
    )
}
