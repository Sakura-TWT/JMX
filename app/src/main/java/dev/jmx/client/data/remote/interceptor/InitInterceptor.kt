package dev.jmx.client.data.remote.interceptor

import dev.jmx.client.data.remote.annotation.GInit
import dev.jmx.client.store.InitManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

class InitInterceptor(
    private val initManager: InitManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val invocation = chain.request().tag(Invocation::class.java)
        val gInitAnnotation = invocation?.method()?.getAnnotation(GInit::class.java)
        if (gInitAnnotation == null) {
            if (!initManager.deferred.isCompleted) {
                runBlocking {
                    initManager.deferred.await()
                }
            }
        }
        return chain.proceed(chain.request())
    }
}