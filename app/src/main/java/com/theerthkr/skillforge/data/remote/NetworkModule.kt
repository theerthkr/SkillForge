package com.theerthkr.skillforge.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

/**
 * Tiny manual DI: a single lazily-built Retrofit + API instance.
 * No Hilt/Koin for a 3-4hr take-home — this keeps the app simple
 * while still keeping network setup out of the UI layer.
 */
object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    val api: SkillforgeApi by lazy {
        Retrofit.Builder()
            .baseUrl(SkillforgeApi.BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(SkillforgeApi::class.java)
    }
}
