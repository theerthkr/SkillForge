package com.theerthkr.skillforge.data.remote

import com.theerthkr.skillforge.data.model.SkillforgeResponse
import retrofit2.http.GET

interface SkillforgeApi {

    // The whole catalog lives at this one URL — it's not a REST path per resource,
    // so we just GET the static JSON and do all the "querying" (find course by id,
    // find lesson by id) on-device after it's fetched.
    @GET("android-assesment/notes/refs/heads/main/data.json")
    suspend fun getCatalog(): SkillforgeResponse

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/"
    }
}
