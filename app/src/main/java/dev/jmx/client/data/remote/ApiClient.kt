package dev.jmx.client.data.remote

import dev.jmx.client.data.remote.converter.PrimitiveToRequestBodyConverterFactory
import dev.jmx.client.data.remote.converter.ResponseConverterFactory
import dev.jmx.client.data.remote.interceptor.BaseUrlInterceptor
import dev.jmx.client.data.remote.interceptor.InitInterceptor
import dev.jmx.client.data.remote.interceptor.ToastInterceptor
import dev.jmx.client.data.remote.interceptor.TokenInterceptor
import dev.jmx.client.storage.CookieStorage
import dev.jmx.client.task.AppInitTask
import dev.jmx.client.task.AppTaskInfo
import dev.jmx.client.utils.log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient(
    baseUrlInterceptor: BaseUrlInterceptor,
    toastInterceptor: ToastInterceptor,
    tokenInterceptor: TokenInterceptor,
    initInterceptor: InitInterceptor,
    private val scalarsConverterFactory: ScalarsConverterFactory,
    private val responseConverterFactory: ResponseConverterFactory,
    private val primitiveToRequestBodyConverterFactory: PrimitiveToRequestBodyConverterFactory,
    private val cookieStorage: CookieStorage
) : AppInitTask {
    private val appTaskInfo = AppTaskInfo(
        taskName = "Remote API client setup",
        sort = 1
    )
    private var cookieList = listOf<Cookie>()
    private val cookieJar = object : CookieJar {
        override fun saveFromResponse(
            url: HttpUrl,
            cookies: List<Cookie>
        ) {
            cookieList =
                (cookieList + cookies).associateBy { "${it.domain}:${it.path}:${it.name}" }.values.toList()
            cookieStorage.set(cookieList)
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieList
        }
    }

    private val okHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(initInterceptor)
            .addInterceptor(baseUrlInterceptor)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(toastInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .cookieJar(cookieJar)
            .build()

    private val retrofitClient: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://placeholder.com/")
            .client(okHttpClient)
            .addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(responseConverterFactory)
            .addConverterFactory(primitiveToRequestBodyConverterFactory)
            .build()
    }

    fun <T> createService(cls: Class<T>): T {
        return retrofitClient.create(cls)
    }

    fun clearCookie() {
        cookieList = listOf()
        cookieStorage.remove()
    }

    fun upsertCookie(cookie: Cookie) {
        cookieList =
            (cookieList + cookie).associateBy { "${it.domain}:${it.path}:${it.name}" }.values.toList()
        cookieStorage.set(cookieList)
    }

    override suspend fun init() {
        log("Remote API client init start")
        log("Restoring API cookies")
        cookieList = cookieStorage.get()
        log("API cookies restored")
        log("Remote API client init finished")
    }

    override fun getAppTaskInfo(): AppTaskInfo = appTaskInfo
}
