package com.golfperformance.app.di

import android.content.Context
import com.golfperformance.app.data.local.AppDatabase
import com.golfperformance.app.data.local.PlayerDao
import com.golfperformance.app.data.remote.ApiService
import com.golfperformance.app.data.remote.MockInterceptor
import com.golfperformance.app.repository.PlayerRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.dsl.module

val appModule = module {
    single { provideOkHttpClient(get()) }
    single { provideRetrofit(get()).create(ApiService::class.java) }
    single { AppDatabase.getInstance(get()) }
    single { get<AppDatabase>().playerDao() }
    single { PlayerRepository(get(), get()) }
}

fun provideOkHttpClient(context: Context): OkHttpClient {
    val logging = HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BASIC) }
    return OkHttpClient.Builder()
        .addInterceptor(MockInterceptor(context))
        .addInterceptor(logging)
        .build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    return Retrofit.Builder()
        .baseUrl("https://api.local/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
}




