package com.theerthkr.skillforge.data.repository

import com.theerthkr.skillforge.data.model.Category
import com.theerthkr.skillforge.data.model.Course
import com.theerthkr.skillforge.data.model.Lesson
import com.theerthkr.skillforge.data.remote.NetworkModule
import com.theerthkr.skillforge.data.remote.SkillforgeApi

/**
 * The API returns the ENTIRE catalog in one call (categories -> courses -> lessons).
 * We fetch it once and cache it in memory, so navigating Home -> CourseDetail -> Lesson
 * doesn't re-hit the network — we just look up the course/lesson by id from the cache.
 */
class SkillforgeRepository(
    private val api: SkillforgeApi = NetworkModule.api
) {
    private var cachedCategories: List<Category>? = null

    suspend fun getCategories(forceRefresh: Boolean = false): List<Category> {
        cachedCategories?.takeIf { !forceRefresh }?.let { return it }
        val response = api.getCatalog()
        cachedCategories = response.categories
        return response.categories
    }

    suspend fun getCourseById(courseId: String): Course? {
        val categories = getCategories()
        return categories.flatMap { it.courses }.firstOrNull { it.id == courseId }
    }

    suspend fun getLessonById(courseId: String, lessonId: String): Lesson? {
        return getCourseById(courseId)?.lessons?.firstOrNull { it.id == lessonId }
    }
}
