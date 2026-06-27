package com.theerthkr.skillforge.data.repository

import com.theerthkr.skillforge.data.model.Category
import com.theerthkr.skillforge.data.model.Course
import com.theerthkr.skillforge.data.model.Lesson
import com.theerthkr.skillforge.data.remote.NetworkModule
import com.theerthkr.skillforge.data.remote.SkillforgeApi
import com.theerthkr.skillforge.data.model.SkillforgeResponse
import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * The API returns the ENTIRE catalog in one call (categories -> courses -> lessons).
 * We fetch it once and cache it in memory, so navigating Home -> CourseDetail -> Lesson
 * doesn't re-hit the network — we just look up the course/lesson by id from the cache.
 */
class SkillforgeRepository(
    private val api: SkillforgeApi = NetworkModule.api
) {
    companion object {
        val instance: SkillforgeRepository by lazy { SkillforgeRepository() }
    }

    private var cachedCategories: List<Category>? = null
    private var appContext: Context? = null
    private var prefs: SharedPreferences? = null

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    fun init(context: Context) {
        appContext = context.applicationContext
        prefs = context.getSharedPreferences("skillforge_prefs", Context.MODE_PRIVATE)
    }

    suspend fun getCategories(forceRefresh: Boolean = false): List<Category> {
        cachedCategories?.takeIf { !forceRefresh }?.let { return it }
        
        return try {
            val response = api.getCatalog()
            cachedCategories = response.categories
            saveToCache(response)
            response.categories
        } catch (e: Exception) {
            val cached = loadFromCache()
            if (cached != null) {
                cachedCategories = cached.categories
                cached.categories
            } else {
                throw e
            }
        }
    }

    private fun saveToCache(response: SkillforgeResponse) {
        val context = appContext ?: return
        try {
            val file = File(context.filesDir, "catalog_cache.json")
            val jsonString = json.encodeToString(response)
            file.writeText(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadFromCache(): SkillforgeResponse? {
        val context = appContext ?: return null
        return try {
            val file = File(context.filesDir, "catalog_cache.json")
            if (file.exists()) {
                val jsonString = file.readText()
                json.decodeFromString<SkillforgeResponse>(jsonString)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun unlockCourse(courseId: String) {
        prefs?.edit()?.putBoolean("unlocked_$courseId", true)?.apply()
    }

    fun isCourseUnlocked(courseId: String): Boolean {
        return prefs?.getBoolean("unlocked_$courseId", false) ?: false
    }

    fun saveNote(courseId: String, lessonId: String, note: String) {
        prefs?.edit()?.putString("note_${courseId}_$lessonId", note)?.apply()
    }

    fun getNote(courseId: String, lessonId: String): String {
        return prefs?.getString("note_${courseId}_$lessonId", "") ?: ""
    }

    suspend fun getCourseById(courseId: String): Course? {
        val categories = getCategories()
        return categories.flatMap { it.courses }.firstOrNull { it.id == courseId }
    }

    suspend fun getLessonById(courseId: String, lessonId: String): Lesson? {
        return getCourseById(courseId)?.lessons?.firstOrNull { it.id == lessonId }
    }
}
