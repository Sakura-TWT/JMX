package dev.jmx.client.utils

import android.util.Log

inline fun <reified T> T.log(msg: String) {
    Log.d("[JMX] ${T::class.java.simpleName}", msg)
}
