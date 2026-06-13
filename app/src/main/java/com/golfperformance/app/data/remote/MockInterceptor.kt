package com.golfperformance.app.data.remote

import android.content.Context
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Simple interceptor that serves a mock players JSON from the app assets for the /players endpoint.
 * This simulates a REST API without relying on external services.
 */
class MockInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (path.endsWith("/players")) {
            val json = context.assets.open("mock_players.json").bufferedReader().use { it.readText() }
            val body = json.toResponseBody("application/json".toMediaType())
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(body)
                .build()
        }

        return chain.proceed(request)
    }
}

